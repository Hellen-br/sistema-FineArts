package finearts.domain;

import java.time.LocalDate;
import java.util.UUID;

/** Certificado digital de autenticidade (RF011). */
public final class Certificado {
    private final String codigo;
    private final ObraDeArte obra;
    private final int compradorId;
    private final LocalDate dataEmissao;

    public Certificado(String codigo, ObraDeArte obra, int compradorId, LocalDate dataEmissao) {
        this.codigo = codigo;
        this.obra = obra;
        this.compradorId = compradorId;
        this.dataEmissao = dataEmissao;
    }

    public static Certificado emitir(ObraDeArte obra, int compradorId) {
        String codigo = "CERT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return new Certificado(codigo, obra, compradorId, LocalDate.now());
    }

    public String getCodigo() { return codigo; }
    public ObraDeArte getObra() { return obra; }
    public int getCompradorId() { return compradorId; }
    public LocalDate getDataEmissao() { return dataEmissao; }
}
