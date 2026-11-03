package finearts.domain;

/** Perfil do cliente (coluna clientes.tipo_cliente, gravada em minúsculas). */
public enum PerfilCliente {
    APRECIADOR, COLECIONADOR, CURADOR;

    public String paraBanco() {
        return name().toLowerCase();
    }

    public static PerfilCliente deBanco(String valor) {
        return valor == null ? null : valueOf(valor.toUpperCase());
    }
}
