# FineArts – Núcleo de regras de negócio (Etapa 6)

Versão refatorada do sistema desktop **FineArts** (venda de obras de arte originais),
preparada para ser reutilizada pelo sistema **web**. Esta etapa **não contém Swing**:
somente domínio, serviços, persistência e testes no `main()`.

## Estrutura

```
src/main/java/finearts/
├── domain/        entidades, objetos-valor, enums e exceções de negócio
├── repository/    contratos (interfaces) + memoria/ (testes) + jdbc/ (MySQL)
├── service/       casos de uso: autenticação, cadastro, catálogo, compra, moderação
├── seguranca/     PasswordEncoder (PBKDF2)
├── infra/         configuração e fábrica de conexões
└── app/           raiz de composição e testes executados pelo main()
db/FineArts_schema.sql      script do banco + dados iniciais
config/                     credenciais (application.properties NÃO vai ao Git)
docs/                       relatório da Etapa 6
```

## Como executar (NetBeans)

1. **File → Open Project** e selecione a pasta deste projeto (projeto Maven).
2. Execute `db/FineArts_schema.sql` no MySQL (cria o banco `finearts` com dados de teste).
3. Confira `config/application.properties` (copie de `application.properties.example`).
4. Botão **Run** (classe principal `finearts.app.Main`).

Sem MySQL, os 14 testes de regras de negócio rodam em memória e os testes de integração são ignorados.

## Testes automatizados (Etapa 7)

Testes unitários e de serviço com **JUnit 5** em `src/test/java` (147 execuções, sem necessidade de banco).

- NetBeans: botão direito no projeto → **Test** (Alt+F6).  Terminal: `mvn test`.
- Plano de testes: `docs/Plano_de_Testes_FineArts.docx`.
- Versionar os testes no repositório: `bash scripts/git-etapa7.sh`.

## Contas de teste (após executar o script SQL)

| E-mail | Senha | Perfil |
|---|---|---|
| admin@finearts.com | admin123 | Administrador |
| ana@finearts.com / carlos@finearts.com | 123456 | Artista (aprovado) |
| mariana@finearts.com / joao@email.com | 123456 | Cliente |

## Git/GitHub

```bash
bash scripts/git-setup.sh https://github.com/SEU_USUARIO/finearts-core.git
```
