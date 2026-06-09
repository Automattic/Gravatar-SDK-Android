"""
Sky Identity Check - Analytics Module
========================================

Privacy-first analytics for tracking events without storing sensitive data.

Features:
  - Event tracking (opt-in)
  - Firestore integration (optional)
  - GDPR compliant (no email storage)
  - Hash-based identification

Author: Sky Meilin
License: MPL 2.0
"""

import logging
from typing import Optional, Dict, Any
from datetime import datetime
from enum import Enum
import os

logger = logging.getLogger(__name__)

# ============================================================================
# CONFIGURATION
# ============================================================================

FIRESTORE_PROJECT_ID = os.getenv("FIRESTORE_PROJECT_ID", "")
FIRESTORE_ENABLED = bool(FIRESTORE_PROJECT_ID)

if FIRESTORE_ENABLED:
    try:
        from firebase_admin import initialize_app, firestore
        import firebase_admin
        
        if not firebase_admin._apps:
            firebase_admin.initialize_app()
        
        db = firestore.client()
        logger.info("Firestore initialized")
    except ImportError:
        logger.warning("Firebase Admin SDK not installed. Firestore disabled.")
        FIRESTORE_ENABLED = False
    except Exception as e:
        logger.warning(f"Firestore initialization failed: {e}. Disabled.")
        FIRESTORE_ENABLED = False
else:
    logger.info("Firestore not configured. Analytics will log locally only.")


# ============================================================================
# ENUMS
# ============================================================================


class EventType(str, Enum):
    """Analytics event types"""
    
    IDENTITY_CHECK_COMPLETED = "identity_check_completed"
    SHA256_GENERATED = "sha256_generated"
    MD5_GENERATED = "md5_generated"
    API_CALL = "api_call"
    WEB_VISIT = "web_visit"
    ERROR = "error"
    CTA_CLICKED = "cta_clicked"


class Platform(str, Enum):
    """Platform identifiers"""
    
    WEB = "web"
    API = "api"
    MOBILE = "mobile"
    ANDROID = "android"
    IOS = "ios"


# ============================================================================
# MODELS
# ============================================================================


class AnalyticsEvent:
    """Analytics event"""
    
    def __init__(
        self,
        event_type: str,
        email_hash: Optional[str] = None,
        platform: Optional[str] = None,
        metadata: Optional[Dict[str, Any]] = None,
    ):
        self.event_type = event_type
        self.email_hash = email_hash
        self.platform = platform or Platform.WEB
        self.metadata = metadata or {}
        self.timestamp = datetime.utcnow().isoformat()
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for storage"""
        return {
            "event_type": self.event_type,
            "email_hash": self.email_hash,
            "platform": self.platform,
            "metadata": self.metadata,
            "timestamp": self.timestamp,
        }


# ============================================================================
# ANALYTICS CLIENT
# ============================================================================


class AnalyticsClient:
    """Analytics client for tracking events"""
    
    def __init__(self):
        self.firestore_enabled = FIRESTORE_ENABLED
        self.collection_name = "analytics_events"
        logger.info(f"AnalyticsClient initialized (Firestore: {self.firestore_enabled})")
    
    async def track(self, event: AnalyticsEvent) -> None:
        """Track an analytics event"""
        
        try:
            self._log_event(event)
            
            if self.firestore_enabled:
                await self._store_firestore(event)
        
        except Exception as e:
            logger.error(f"Error tracking event: {str(e)}")
    
    def _log_event(self, event: AnalyticsEvent) -> None:
        """Log event locally"""
        logger.info(
            f"[ANALYTICS] {event.event_type} | "
            f"Platform: {event.platform} | "
            f"Hash: {event.email_hash[:8] if event.email_hash else 'N/A'}... | "
            f"Metadata: {event.metadata}"
        )
    
    async def _store_firestore(self, event: AnalyticsEvent) -> None:
        """Store event in Firestore"""
        
        if not FIRESTORE_ENABLED:
            return
        
        try:
            db.collection(self.collection_name).add(event.to_dict())
            logger.debug(f"Event stored in Firestore: {event.event_type}")
        except Exception as e:
            logger.error(f"Failed to store event in Firestore: {str(e)}")


# ============================================================================
# CONVENIENCE FUNCTIONS
# ============================================================================


async def track_identity_check(
    email_hash: str,
    platform: str = Platform.WEB,
) -> None:
    """Track identity check event"""
    
    client = AnalyticsClient()
    event = AnalyticsEvent(
        event_type=EventType.IDENTITY_CHECK_COMPLETED,
        email_hash=email_hash,
        platform=platform,
    )
    await client.track(event)


async def track_error(
    error_message: str,
    platform: str = Platform.WEB,
) -> None:
    """Track error event"""
    
    client = AnalyticsClient()
    event = AnalyticsEvent(
        event_type=EventType.ERROR,
        platform=platform,
        metadata={"error": error_message[:100]},
    )
    await client.track(event)
