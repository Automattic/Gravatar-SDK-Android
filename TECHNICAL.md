# Sky Identity Check - Technical Architecture
# Exportable für Google Sheets

## API ENDPOINTS

| Endpoint | Method | Query/Body | Response | Zweck |
|----------|--------|-----------|----------|--------|
| `/health` | GET | - | `{status: ok, version: 1.0.0}` | Health Check |
| `/api/v1/sha256` | GET | `email=...` | `{hash_value: abc..., hash_type: sha256}` | SHA-256 Hash |
| `/api/v1/md5` | GET | `email=...` | `{hash_value: def..., hash_type: md5}` | MD5 Hash (Gravatar) |
| `/api/v1/check` | POST | `{email: str}` | `{sha256, md5, gravatar_url}` | Full Check |
| `/api/v1/events` | POST | `{event_type, email_hash}` | `{message: tracked}` | Analytics |
| `/docs` | GET | - | OpenAPI | Swagger Dokumentation |


## HASHING LOGIC

```
Input: "Max@Example.COM"
  ↓
Normalize: email.strip().lower()
  ↓
Result: "max@example.com"
  ↓
SHA-256 Hash: sha256("max@example.com").hexdigest()
MD5 Hash: md5("max@example.com").hexdigest()
  ↓
Gravatar URL: "https://gravatar.com/avatar/{md5}?s=240&d=mp&r=g"
```


## SERVICES

| Service | Port | Technologie | Zweck |
|---------|------|-----------|--------|
| Web UI | 7860 | Gradio | Interactive UI für Users |
| REST API | 8000 | FastAPI | Programmatic Access |
| Analytics | - | Firestore (opt) | Event Tracking |


## DEPLOYMENT

| Platform | Befehl | Konfiguration |
|----------|--------|----------------|
| Lokal | `docker-compose up -d` | `.env` (optional) |
| Docker | `docker run -p 7860:7860 -p 8000:8000 sky-identity-check` | ENV Variables |
| Hugging Face | Push to `spaces/USERNAME/sky-identity-check` | README.md YAML |
| Cloud Run | `gcloud run deploy sky-identity-check --source .` | Cloud Console |


## ENVIRONMENT VARIABLES

| Variable | Default | Beschreibung |
|----------|---------|---------------|
| `ENABLE_ANALYTICS` | false | Analytics aktivieren (true/false) |
| `FIRESTORE_PROJECT_ID` | - | GCP Project ID für Firestore |
| `GRADIO_PORT` | 7860 | Port für Web UI |
| `API_PORT` | 8000 | Port für REST API |
| `ENVIRONMENT` | development | Environment (development/staging/production) |


## DATEN-FLOW

```
┌─────────────────────┐
│ Benutzer Input      │
│ (E-Mail)            │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Validierung         │
│ (Email Format)      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Normalisierung      │
│ (trim + lowercase)  │
└──────────┬──────────┘
           │
           ▼
┌──────────────────────────────┐
│ Hash Generierung             │
├──────────────────────────────┤
│ • SHA-256 (secure)           │
│ • MD5 (Gravatar)             │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ Gravatar URL Generierung     │
│ {md5}?s=240&d=mp&r=g         │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ Output Display               │
├──────────────────────────────┤
│ • SHA-256 Hash               │
│ • MD5 Hash                   │
│ • Gravatar URL               │
│ • Avatar Image               │
│ • CTA Links                  │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ Analytics (Optional)         │
├──────────────────────────────┤
│ • Event Type                 │
│ • Email Hash                 │
│ • Platform                   │
│ • Timestamp                  │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ Firestore (wenn aktiviert)   │
│ oder lokales Logging         │
└──────────────────────────────┘
```


## SECURITY & PRIVACY

| Aspekt | Status | Details |
|--------|--------|----------|
| Email Storage | ✅ NEIN | Nur Hashes gespeichert |
| Cookies | ✅ NEIN | Stateless API |
| Tracking Pixels | ✅ NEIN | Opt-in Analytics |
| External APIs | ✅ NEIN | Alles lokal (außer Gravatar URLs) |
| GDPR Compliant | ✅ JA | Minimal data collection |
| Open Source | ✅ JA | MPL 2.0 License |


## BEISPIEL: API CALLS

### 1. SHA-256 Hash abrufen
```
GET /api/v1/sha256?email=max@example.com

Response:
{
  "email": "max@example.com",
  "normalized_email": "max@example.com",
  "hash_value": "abc123def456...",
  "hash_type": "sha256",
  "timestamp": "2024-06-09T14:46:14Z"
}
```

### 2. Full Identity Check
```
POST /api/v1/check
Content-Type: application/json

{
  "email": "max@example.com",
  "include_gravatar": true
}

Response:
{
  "email": "max@example.com",
  "normalized_email": "max@example.com",
  "sha256": "abc123def456...",
  "md5": "ghi789jkl012...",
  "gravatar_url": "https://gravatar.com/avatar/ghi789jkl012...",
  "timestamp": "2024-06-09T14:46:14Z"
}
```

### 3. Analytics Event
```
POST /api/v1/events
Content-Type: application/json

{
  "event_type": "identity_check_completed",
  "email_hash": "abc123def456",
  "platform": "mobile",
  "metadata": {"app_version": "1.0.0"}
}

Response:
{
  "message": "Event tracked"
}
```


## PERFORMANCE

| Metrik | Wert |
|--------|------|
| Hash Generation | < 10ms |
| API Response | < 50ms |
| Container Start | < 5s |
| Memory Usage | ~150MB |
| Container Size | ~300MB |


## FIRESTORE STRUKTUR (Optional)

```
Collection: analytics_events

Document:
{
  event_type: "identity_check_completed",
  email_hash: "abc123def456...",
  platform: "web",
  metadata: {
    include_gravatar: true
  },
  timestamp: "2024-06-09T14:46:14Z"
}
```


## FILES ÜBERSICHT

| Datei | Größe | Zweck |
|-------|-------|--------|
| `app.py` | ~600 Zeilen | Gradio Web UI |
| `api.py` | ~400 Zeilen | FastAPI REST |
| `analytics.py` | ~250 Zeilen | Event Tracking |
| `Dockerfile` | ~30 Zeilen | Container |
| `requirements.txt` | ~7 Zeilen | Dependencies |


## TESTING

```bash
# Unit Tests
pytest tests/test_api.py
pytest tests/test_analytics.py

# Integration Tests
pytest tests/integration/

# Manual Testing
curl http://localhost:8000/health
curl http://localhost:7860/config
```


## ROADMAP

- [x] Gradio Web UI
- [x] FastAPI REST API
- [x] Analytics Module
- [ ] Rate Limiting
- [ ] API Keys / Authentication
- [ ] Admin Dashboard
- [ ] Redis Caching
- [ ] Prometheus Monitoring
- [ ] Multi-language Support


## SUPPORT

- 📧 Email: kontakt@skymeiin.dev
- 🐛 Issues: GitHub Issues
- 💬 Discussions: GitHub Discussions

---

**Sky Meilin | One World. One Sky. One Identity.**
