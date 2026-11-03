package finearts.seguranca;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2-HMAC-SHA256 com sal aleatório. Formato: PBKDF2$iterações$salBase64$hashBase64. */
public class Pbkdf2PasswordEncoder implements PasswordEncoder {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIXO = "PBKDF2";
    private static final int TAMANHO_SAL = 16;
    private static final int TAMANHO_HASH_BITS = 256;

    private final int iteracoes;
    private final SecureRandom aleatorio = new SecureRandom();

    public Pbkdf2PasswordEncoder() { this(120_000); }

    public Pbkdf2PasswordEncoder(int iteracoes) { this.iteracoes = iteracoes; }

    @Override
    public String codificar(String senhaPura) {
        byte[] sal = new byte[TAMANHO_SAL];
        aleatorio.nextBytes(sal);
        byte[] hash = derivar(senhaPura, sal, iteracoes);
        Base64.Encoder b64 = Base64.getEncoder();
        return PREFIXO + "$" + iteracoes + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
    }

    @Override
    public boolean confere(String senhaPura, String senhaCodificada) {
        if (senhaPura == null || senhaCodificada == null) {
            return false;
        }
        String[] partes = senhaCodificada.split("\\$");
        if (partes.length != 4 || !PREFIXO.equals(partes[0])) {
            return false;
        }
        try {
            int it = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            return MessageDigest.isEqual(esperado, derivar(senhaPura, sal, it));
        } catch (IllegalArgumentException e) {
            return false; // formato corrompido
        }
    }

    private static byte[] derivar(String senha, byte[] sal, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, iteracoes, TAMANHO_HASH_BITS);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algoritmo de hash indisponível", e);
        }
    }
}
