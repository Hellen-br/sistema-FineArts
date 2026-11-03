#!/usr/bin/env bash
# Etapa 7 - versiona os testes JUnit no MESMO repositório criado na Etapa 6.
# Uso:  bash scripts/git-etapa7.sh        (na raiz do projeto, onde já existe a pasta .git)
# Se o remoto "origin" estiver configurado, também faz o push.
set -e
cd "$(dirname "$0")/.."
if [ ! -d .git ]; then
  echo "Repositório não encontrado. Execute antes: bash scripts/git-setup.sh <URL_DO_GITHUB>"; exit 1
fi

git add src/main/java/finearts/domain/ComissaoPlataforma.java src/main/java/finearts/domain/Parcelamento.java \
        src/main/java/finearts/domain/Pedido.java src/main/java/finearts/service
git commit -m "feat(domain): regras de cálculo isoladas (ComissaoPlataforma e Parcelamento) para facilitar testes"

git add pom.xml
git commit -m "build: adiciona JUnit 5 (Jupiter) e Surefire ao pom.xml"

git add src/test/java/finearts/testes src/test/java/finearts/domain
git commit -m "test(domain): testes unitários de CPF, comissão, parcelamento, carrinho, pedido, status e filtros"

git add src/test/java/finearts/seguranca src/test/java/finearts/service
git commit -m "test(service): testes de validadores, PBKDF2 e serviços com repositórios em memória"

git add docs scripts
git commit -m "docs: plano de testes e script de versionamento da Etapa 7"

# Evidência do versionamento (gerada a partir do SEU repositório)
git log --stat --date=short --pretty=format:'%h | %ad | %an | %s' > docs/evidencia-versionamento.txt
git add docs/evidencia-versionamento.txt
git commit -m "docs: evidência do versionamento do projeto de testes"

if git remote | grep -q origin; then git push; else echo "Sem remoto 'origin': faça git remote add origin <URL> e git push -u origin main"; fi
git log --oneline | head -15
