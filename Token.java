public class Token {
    private final TiposTokens tipo;
    private final String valor;

    public Token(TiposTokens tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public TiposTokens getTipo(){
        return tipo;
    }

    public String getValor(){
        return valor;
    }

    @Override
    public String toString() {
        return "Token{" + "tipo=" + tipo + ", valor=" + valor +"}";
    }

}
