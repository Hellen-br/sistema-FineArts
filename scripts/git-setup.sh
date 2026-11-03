#!/usr/bin/env bash
# Cria o repositório Git local com um histórico organizado de commits e (opcionalmente) envia ao GitHub.
# Uso:  bash scripts/git-setup.sh [URL_DO_REPOSITORIO_NO_GITHUB]
# Pré-requisitos: git instalado e identidade configurada (git config user.name / user.email).
set -e
cd "$(dirname "$0")/.."

git init -b main
git add pom.xml .gitignore README.md config/application.properties.example scripts
git commit -m "chore: estrutura Maven do projeto, .gitignore e README"

git add src/main/java/finearts/domain
git commit -m "feat(domain): entidades, objetos-valor, enums e exceções de negócio"

git add src/main/java/finearts/repository/*.java src/main/java/finearts/repository/memoria
git commit -m "feat(repository): contratos de persistência e implementações em memória"

git add src/main/java/finearts/seguranca
git commit -m "feat(seguranca): PasswordEncoder e hash PBKDF2 (substitui senha em texto puro)"

git add src/main/java/finearts/service
git commit -m "feat(service): serviços de autenticação, cadastro, catálogo, compra e moderação"

git add src/main/java/finearts/infra src/main/java/finearts/repository/jdbc db/FineArts_schema.sql
git commit -m "feat(jdbc): persistência MySQL (JdbcExecutor, repositórios) e script SQL com dados iniciais"

git add src/main/java/finearts/app
git commit -m "test: composição de dependências e testes no main() (negócio e integração MySQL)"

git add docs
git commit -m "docs: relatório da Etapa 6 e documentos de projeto"

git add -A
git diff --cached --quiet || git commit -m "chore: ajustes finais"

if [ -n "$1" ]; then
  git remote add origin "$1"
  git push -u origin main
else
  echo "Repositório local criado. Para enviar ao GitHub:"
  echo "  git remote add origin https://github.com/SEU_USUARIO/finearts-core.git"
  echo "  git push -u origin main"
fi
git log --oneline
