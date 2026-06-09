"""
Sky Identity Check - REST API
================================

FastAPI REST endpoints für die Gravatar Identity Check App.
Ermöglicht Integration mit Mobile Apps, 3rd-party Services, etc.

Endpoints:
  GET  /api/v1/sha256?email=...      - SHA-256 Hash
  GET  /api/v1/md5?email=...         - MD5 Hash
  POST /api/v1/check                 - Full Identity Check
  POST /api/v1/events                - Analytics Events
  GET  /health                        - Health Check
  GET  /docs                          - OpenAPI Docs

Author: Sky Meilin
License: MPL 2.0
"""

from fastapi import FastAPI, HTTPException, BackgroundTasks, Query
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr, validator
from typing import Optional, Dict, Any
import hashlib
import logging
from datetime import datetime
import os

from analytics import AnalyticsClient

# ============================================================================
# CONFIGURATION
# ============================================================================

APP_VERSION = "1.0.0"
APP_TITLE = "Sky Identity Check API"

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

# Initialize FastAPI
app = FastAPI(
    title=APP_TITLE,
    version=APP_VERSION,
    description="REST API for Gravatar Digital Identity Checks",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
)

# Initialize Analytics (optional)
ENABLE_ANALYTICS = os.getenv("ENABLE_ANALYTICS", "false").lower() == "true"
analytics_client = AnalyticsClient() if ENABLE_ANALYTICS else None

# CORS Configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ============================================================================
# MODELS
# ============================================================================


class EmailCheckRequest(BaseModel):
    """Request model for identity check"""

    email: EmailStr
    include_gravatar: bool = True

    @validator("email")
    def normalize_email(cls, v):
        return v.strip().lower()


class IdentityCheckResponse(BaseModel):
    """Response model for identity check"""

    email: str
    normalized_email: str
    sha256: str
    md5: str
    gravatar_url: str
    timestamp: str


class HashResponse(BaseModel):
    """Response model for hash endpoints"""

    email: str
    normalized_email: str
    hash_value: str
    hash_type: str
    timestamp: str


class AnalyticsEvent(BaseModel):
    """Analytics event model"""

    event_type: str
    email_hash: Optional[str] = None
    platform: Optional[str] = None
    metadata: Optional[Dict[str, Any]] = None


class HealthResponse(BaseModel):
    """Health check response"""

    status: str
    version: str
    timestamp: str
    analytics_enabled: bool


# ============================================================================
# UTILITY FUNCTIONS
# ============================================================================


def validate_email_format(email: str) -> tuple[bool, str]:
    """Validate email format"""
    if not email or "@" not in email:
        return False, "Invalid email format"
    if len(email) > 254:
        return False, "Email too long"
    return True, ""


def generate_sha256(email: str) -> str:
    """Generate SHA-256 hash"""
    return hashlib.sha256(email.encode("utf-8")).hexdigest()


def generate_md5(email: str) -> str:
    """Generate MD5 hash"""
    return hashlib.md5(email.encode("utf-8")).hexdigest()


def generate_gravatar_url(md5_hash: str, size: int = 240) -> str:
    """Generate Gravatar URL"""
    return f"https://www.gravatar.com/avatar/{md5_hash}?s={size}&d=mp&r=g"


# ============================================================================
# ENDPOINTS
# ============================================================================


@app.get("/health", response_model=HealthResponse)
async def health_check() -> HealthResponse:
    """Health check endpoint"""
    return HealthResponse(
        status="ok",
        version=APP_VERSION,
        timestamp=datetime.now().isoformat(),
        analytics_enabled=ENABLE_ANALYTICS,
    )


@app.get("/api/v1/sha256", response_model=HashResponse)
async def get_sha256(
    email: str = Query(..., description="Email address"),
    background_tasks: BackgroundTasks = None,
) -> HashResponse:
    """Generate SHA-256 hash from email"""
    
    try:
        normalized_email = email.strip().lower()
        is_valid, error = validate_email_format(normalized_email)
        if not is_valid:
            raise HTTPException(status_code=400, detail=error)
        
        sha256_hash = generate_sha256(normalized_email)
        
        if ENABLE_ANALYTICS and background_tasks and analytics_client:
            background_tasks.add_task(
                analytics_client.track,
                AnalyticsEvent(
                    event_type="sha256_generated",
                    email_hash=sha256_hash,
                    platform="api",
                )
            )
        
        logger.info(f"SHA-256 generated for: {normalized_email[:10]}...")
        
        return HashResponse(
            email=email,
            normalized_email=normalized_email,
            hash_value=sha256_hash,
            hash_type="sha256",
            timestamp=datetime.now().isoformat(),
        )
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error generating SHA-256: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal server error")


@app.get("/api/v1/md5", response_model=HashResponse)
async def get_md5(
    email: str = Query(..., description="Email address"),
    background_tasks: BackgroundTasks = None,
) -> HashResponse:
    """Generate MD5 hash from email (Gravatar compatible)"""
    
    try:
        normalized_email = email.strip().lower()
        is_valid, error = validate_email_format(normalized_email)
        if not is_valid:
            raise HTTPException(status_code=400, detail=error)
        
        md5_hash = generate_md5(normalized_email)
        
        if ENABLE_ANALYTICS and background_tasks and analytics_client:
            background_tasks.add_task(
                analytics_client.track,
                AnalyticsEvent(
                    event_type="md5_generated",
                    email_hash=md5_hash,
                    platform="api",
                )
            )
        
        logger.info(f"MD5 generated for: {normalized_email[:10]}...")
        
        return HashResponse(
            email=email,
            normalized_email=normalized_email,
            hash_value=md5_hash,
            hash_type="md5",
            timestamp=datetime.now().isoformat(),
        )
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error generating MD5: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal server error")


@app.post("/api/v1/check", response_model=IdentityCheckResponse)
async def check_identity(
    request: EmailCheckRequest,
    background_tasks: BackgroundTasks,
) -> IdentityCheckResponse:
    """Full identity check - generates both hashes and Gravatar URL"""
    
    try:
        normalized_email = request.email.strip().lower()
        is_valid, error = validate_email_format(normalized_email)
        if not is_valid:
            raise HTTPException(status_code=400, detail=error)
        
        sha256_hash = generate_sha256(normalized_email)
        md5_hash = generate_md5(normalized_email)
        gravatar_url = generate_gravatar_url(md5_hash)
        
        if ENABLE_ANALYTICS and background_tasks and analytics_client:
            background_tasks.add_task(
                analytics_client.track,
                AnalyticsEvent(
                    event_type="identity_check_completed",
                    email_hash=md5_hash,
                    platform="api",
                    metadata={"include_gravatar": request.include_gravatar}
                )
            )
        
        logger.info(f"Identity check completed for: {normalized_email[:10]}...")
        
        return IdentityCheckResponse(
            email=request.email,
            normalized_email=normalized_email,
            sha256=sha256_hash,
            md5=md5_hash,
            gravatar_url=gravatar_url,
            timestamp=datetime.now().isoformat(),
        )
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error in identity check: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal server error")


@app.post("/api/v1/events")
async def track_api_event(event: AnalyticsEvent) -> JSONResponse:
    """Track analytics events"""
    
    if not ENABLE_ANALYTICS or not analytics_client:
        return JSONResponse(
            status_code=202,
            content={"message": "Analytics disabled"}
        )
    
    try:
        await analytics_client.track(event)
        return JSONResponse(
            status_code=202,
            content={"message": "Event tracked"}
        )
    except Exception as e:
        logger.error(f"Error tracking event: {str(e)}")
        return JSONResponse(
            status_code=500,
            content={"error": "Failed to track event"}
        )


# ============================================================================
# MAIN
# ============================================================================


if __name__ == "__main__":
    import uvicorn
    
    logger.info(f"Starting {APP_TITLE} (v{APP_VERSION})")
    logger.info(f"Analytics enabled: {ENABLE_ANALYTICS}")
    
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8000,
        log_level="info",
    )
