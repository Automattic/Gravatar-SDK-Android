.PHONY: help build run test clean stop docker-build docker-run docker-stop

help:
	@echo "Sky Identity Check - Development Commands"
	@echo ""
	@echo "Local Development:"
	@echo "  make run              - Run Python app directly"
	@echo "  make install          - Install dependencies"
	@echo "  make lint             - Lint code with flake8"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-build     - Build Docker image"
	@echo "  make docker-run       - Run Docker container"
	@echo "  make docker-stop      - Stop Docker container"
	@echo "  make docker-logs      - View Docker logs"
	@echo ""
	@echo "Docker Compose:"
	@echo "  make compose-up       - Start with Docker Compose"
	@echo "  make compose-down     - Stop Docker Compose"
	@echo ""
	@echo "Maintenance:"
	@echo "  make clean            - Clean build artifacts"
	@echo "  make test             - Run tests (placeholder)"
	@echo ""

install:
	python3.12 -m venv venv
	. venv/bin/activate && pip install -r requirements.txt
	@echo "✅ Dependencies installed"

run:
	python app.py

lint:
	@echo "Checking code style..."
	@python -m flake8 app.py --max-line-length=100 --ignore=E501,W503 || true

test:
	@echo "Running tests..."
	@echo "(No tests defined yet)"

clean:
	find . -type d -name __pycache__ -exec rm -rf {} + 2>/dev/null || true
	find . -type f -name "*.pyc" -delete
	rm -rf build/ dist/ *.egg-info/
	rm -rf .pytest_cache/ .coverage htmlcov/
	@echo "✅ Cleaned"

docker-build:
	docker build -t sky-identity-check:latest .
	@echo "✅ Docker image built"

docker-run:
	docker run -p 7860:7860 --name sky-identity-check sky-identity-check:latest
	@echo "✅ App running on http://localhost:7860"

docker-stop:
	docker stop sky-identity-check 2>/dev/null || true
	docker rm sky-identity-check 2>/dev/null || true
	@echo "✅ Container stopped"

docker-logs:
	docker logs -f sky-identity-check

compose-up:
	docker-compose up -d
	@echo "✅ Services running"
	@echo "📱 App available at http://localhost:7860"

compose-down:
	docker-compose down
	@echo "✅ Services stopped"

all: clean install lint docker-build
	@echo "✅ All done!"
