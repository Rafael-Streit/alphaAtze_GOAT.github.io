# ============================================================
#  setup-dev-env.ps1
#  Richtet Java 26, Maven und Git in Debian WSL ein
#  Ausfuehren mit: powershell -ExecutionPolicy Bypass -File .\setup-dev-env.ps1
# ============================================================

$ErrorActionPreference = "Stop"
$distro = "Debian"

function Run-WSL {
    param([string]$cmd)
    wsl -d $distro -- bash -c $cmd
    if ($LASTEXITCODE -ne 0) {
        Write-Error "WSL-Befehl fehlgeschlagen: $cmd"
        exit 1
    }
}

Write-Host ""
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "  Dev-Umgebung Setup fuer Debian WSL   " -ForegroundColor Cyan
Write-Host "  Java 26 | Maven | Git                " -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

# ------------------------------------------------------------
# 1. Paketlisten aktualisieren
# ------------------------------------------------------------
Write-Host "[1/6] Paketlisten aktualisieren..." -ForegroundColor Yellow
Run-WSL "sudo apt-get update -y"

# ------------------------------------------------------------
# 2. Basis-Pakete + Abhaengigkeiten
# ------------------------------------------------------------
Write-Host "[2/6] Basis-Pakete installieren (curl, wget, tar, git)..." -ForegroundColor Yellow
Run-WSL "sudo apt-get install -y curl wget tar git ca-certificates gnupg"

# ------------------------------------------------------------
# 3. Java 26 via SDKMAN installieren
#    (SDKMAN ist der sauberste Weg fuer nicht-LTS-Versionen)
# ------------------------------------------------------------
Write-Host "[3/6] SDKMAN installieren und Java 26 einrichten..." -ForegroundColor Yellow

$sdkmanSetup = @'
set -e

# SDKMAN installieren falls nicht vorhanden
if [ ! -d "$HOME/.sdkman" ]; then
    curl -s "https://get.sdkman.io" | bash
fi

# SDKMAN in aktuelle Shell laden
export SDKMAN_DIR="$HOME/.sdkman"
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Java 26 installieren (Microsoft Build oder Temurin, je nach Verfuegbarkeit)
# Verfuegbare 26er Builds anzeigen und ersten nehmen
JAVA26=$(sdk list java | grep -oE '[0-9]+\.[0-9]+\.[0-9]+-[a-z]+' | grep '^26' | head -1)

if [ -z "$JAVA26" ]; then
    echo "WARNUNG: Java 26 noch nicht als Stable verfuegbar, nehme Early-Access..."
    JAVA26=$(sdk list java | grep -oE '[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+-[a-z]+' | grep '^26' | head -1)
fi

if [ -z "$JAVA26" ]; then
    echo "Fallback: Installiere Java 26 EA via Temurin-Nightly / Oracle EA..."
    # Oracle OpenJDK 26 EA direkt installieren
    JAVA_VERSION="26"
    JAVA_BUILD="36"
    JAVA_URL="https://download.java.net/java/early_access/jdk${JAVA_VERSION}/${JAVA_BUILD}/GPL/openjdk-${JAVA_VERSION}-ea+${JAVA_BUILD}_linux-x64_bin.tar.gz"
    
    mkdir -p /opt/java
    wget -q --show-progress -O /tmp/jdk26.tar.gz "$JAVA_URL"
    sudo tar -xzf /tmp/jdk26.tar.gz -C /opt/java/
    rm /tmp/jdk26.tar.gz
    JAVA_HOME_PATH=$(ls -d /opt/java/jdk-26*)
    
    # PATH dauerhaft in .bashrc und .profile eintragen
    grep -qxF "export JAVA_HOME=$JAVA_HOME_PATH" ~/.bashrc || echo "export JAVA_HOME=$JAVA_HOME_PATH" >> ~/.bashrc
    grep -qxF 'export PATH=$JAVA_HOME/bin:$PATH' ~/.bashrc || echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
    grep -qxF "export JAVA_HOME=$JAVA_HOME_PATH" ~/.profile || echo "export JAVA_HOME=$JAVA_HOME_PATH" >> ~/.profile
    grep -qxF 'export PATH=$JAVA_HOME/bin:$PATH' ~/.profile || echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.profile
    
    echo "Java 26 EA unter $JAVA_HOME_PATH installiert."
else
    sdk install java "$JAVA26"
    sdk default java "$JAVA26"
    echo "Java $JAVA26 via SDKMAN installiert."
fi
'@

# Skript in WSL-Datei schreiben und ausfuehren
$sdkmanSetup | wsl -d $distro -- bash -c "cat > /tmp/install_java.sh && chmod +x /tmp/install_java.sh && bash /tmp/install_java.sh"

# ------------------------------------------------------------
# 4. Maven installieren (neueste stabile Version via SDKMAN)
# ------------------------------------------------------------
Write-Host "[4/6] Apache Maven installieren..." -ForegroundColor Yellow

$mavenSetup = @'
set -e

# Pruefe ob SDKMAN verfuegbar
if [ -d "$HOME/.sdkman" ]; then
    export SDKMAN_DIR="$HOME/.sdkman"
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    sdk install maven
    sdk default maven latest
    echo "Maven via SDKMAN installiert."
else
    # Fallback: Maven direkt installieren
    MAVEN_VERSION="3.9.9"
    wget -q "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -O /tmp/maven.tar.gz
    sudo tar -xzf /tmp/maven.tar.gz -C /opt/
    sudo ln -sf /opt/apache-maven-${MAVEN_VERSION} /opt/maven
    rm /tmp/maven.tar.gz
    
    grep -qxF 'export M2_HOME=/opt/maven' ~/.bashrc || echo 'export M2_HOME=/opt/maven' >> ~/.bashrc
    grep -qxF 'export PATH=$M2_HOME/bin:$PATH' ~/.bashrc || echo 'export PATH=$M2_HOME/bin:$PATH' >> ~/.bashrc
    grep -qxF 'export M2_HOME=/opt/maven' ~/.profile || echo 'export M2_HOME=/opt/maven' >> ~/.profile
    grep -qxF 'export PATH=$M2_HOME/bin:$PATH' ~/.profile || echo 'export PATH=$M2_HOME/bin:$PATH' >> ~/.profile
    
    echo "Maven ${MAVEN_VERSION} direkt installiert."
fi
'@

$mavenSetup | wsl -d $distro -- bash -c "cat > /tmp/install_maven.sh && chmod +x /tmp/install_maven.sh && bash /tmp/install_maven.sh"

# ------------------------------------------------------------
# 5. Git konfigurieren (global)
# ------------------------------------------------------------
Write-Host "[5/6] Git einrichten..." -ForegroundColor Yellow

$gitUser = Read-Host "  Git Benutzername (fuer git config --global user.name)"
$gitEmail = Read-Host "  Git E-Mail       (fuer git config --global user.email)"

Run-WSL "git config --global user.name '$gitUser'"
Run-WSL "git config --global user.email '$gitEmail'"
Run-WSL "git config --global init.defaultBranch main"
Run-WSL "git config --global core.autocrlf input"
Run-WSL "git config --global pull.rebase false"

# ------------------------------------------------------------
# 6. Versionen pruefen & Ergebnis anzeigen
# ------------------------------------------------------------
Write-Host "[6/6] Installation pruefen..." -ForegroundColor Yellow
Write-Host ""

Write-Host "--- Installierte Versionen ---" -ForegroundColor Green
wsl -d $distro -- bash -lc "java -version 2>&1 | head -1"
wsl -d $distro -- bash -lc "mvn -version 2>&1 | head -1"
wsl -d $distro -- bash -lc "git --version"
Write-Host ""

Write-Host "=======================================" -ForegroundColor Green
Write-Host "  Setup abgeschlossen!                 " -ForegroundColor Green
Write-Host "                                       " -ForegroundColor Green
Write-Host "  Starte eine neue WSL-Session damit   " -ForegroundColor Green
Write-Host "  alle PATH-Aenderungen aktiv sind.    " -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green
Write-Host ""
Write-Host "Tipp: Mit 'wsl -d Debian' WSL oeffnen, dann:" -ForegroundColor Cyan
Write-Host "      java -version   mvn -version   git --version" -ForegroundColor Cyan
Write-Host ""
