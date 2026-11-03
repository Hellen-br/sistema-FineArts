package finearts.domain;

/** Objeto-valor: elimina o par largura/altura que andava solto (Data Clumps). */
public final class Dimensoes {
    private final double larguraCm;
    private final double alturaCm;

    public Dimensoes(double larguraCm, double alturaCm) {
        if (larguraCm <= 0 || alturaCm <= 0) {
            throw new IllegalArgumentException("Dimensões devem ser positivas.");
        }
        this.larguraCm = larguraCm;
        this.alturaCm = alturaCm;
    }

    public double getLarguraCm() { return larguraCm; }
    public double getAlturaCm() { return alturaCm; }

    @Override
    public String toString() { return larguraCm + " x " + alturaCm + " cm"; }
}
