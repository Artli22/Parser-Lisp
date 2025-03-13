import java.util.*;

public class ProcesadorLisp {

    enum TiposTokens {
        Parentesis_Abierto, Parentesis_Cerrado, Numero, Identificador, Cadena, Operador
    }

    static class Token {
        private final TiposTokens tipo;
        private final String valor;

        public Token(TiposTokens tipo, String valor) {
            this.tipo = tipo;
            this.valor = valor;
        }

        public TiposTokens getTipo() { return tipo; }
        public String getValor() { return valor; }

        @Override
        public String toString() { return "Token{" + "tipo=" + tipo + ", valor='" + valor + "'}"; }
    }

    static class Lexer {
        private final String expresion;

        public Lexer(String expresion) {
            this.expresion = expresion;
        }

        public List<Token> tokenizar() {
            if (!isBalanced(expresion)) {
                throw new IllegalArgumentException("Error: La expresión tiene paréntesis desbalanceados");
            }

            List<Token> tokens = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();
            char[] caracteres = expresion.toCharArray();

            for (int i = 0; i < caracteres.length; i++) {
                char c = caracteres[i];

                if (Character.isWhitespace(c)) continue;

                try {
                    if (c == '(') {
                        tokens.add(new Token(TiposTokens.Parentesis_Abierto, "("));
                    } else if (c == ')') {
                        tokens.add(new Token(TiposTokens.Parentesis_Cerrado, ")"));
                    } else if (Character.isDigit(c)) {
                        buffer.append(c);
                        while (i + 1 < caracteres.length && Character.isDigit(caracteres[i + 1])) {
                            buffer.append(caracteres[++i]);
                        }
                        tokens.add(new Token(TiposTokens.Numero, buffer.toString()));
                        buffer.setLength(0);
                    } else if (Character.isLetter(c)) {
                        buffer.append(c);
                        while (i + 1 < caracteres.length && (Character.isLetterOrDigit(caracteres[i + 1]) || caracteres[i + 1] == '-')) {
                            buffer.append(caracteres[++i]);
                        }
                        tokens.add(new Token(TiposTokens.Identificador, buffer.toString()));
                        buffer.setLength(0);
                    } else if (c == '"') {
                        buffer.append(c);
                        while (i + 1 < caracteres.length && caracteres[i + 1] != '"') {
                            buffer.append(caracteres[++i]);
                        }
                        buffer.append('"');
                        tokens.add(new Token(TiposTokens.Cadena, buffer.toString()));
                        buffer.setLength(0);
                    } else if ("+-*/<>=,".indexOf(c) != -1) {
                        tokens.add(new Token(TiposTokens.Operador, String.valueOf(c)));
                    } else {
                        throw new IllegalArgumentException("Error: Caracter inesperado '" + c + "'");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    return Collections.emptyList();
                }
            }
            return tokens;
        }

        private boolean isBalanced(String input) {
            Stack<Character> stack = new Stack<>();
            for (char c : input.toCharArray()) {
                if (c == '(') stack.push(c);
                else if (c == ')') {
                    if (stack.isEmpty()) return false;
                    stack.pop();
                }
            }
            return stack.isEmpty();
        }
    }

    static class Parser {
        private final List<Token> tokens;
        private int currentIndex = 0;

        public Parser(List<Token> tokens) {
            this.tokens = tokens;
        }

        public Object parse() {
            try {
                return parseExpression();
            } catch (Exception e) {
                System.err.println("Error de parsing: " + e.getMessage());
                return null;
            }
        }

        private Object parseExpression() {
            if (currentIndex >= tokens.size()) {
                throw new IllegalArgumentException("Error: Expresión incompleta");
            }
            
            Token token = consume();
            if (token.getTipo() == TiposTokens.Parentesis_Abierto) {
                List<Object> lista = new ArrayList<>();
                while (currentIndex < tokens.size() && peek().getTipo() != TiposTokens.Parentesis_Cerrado) {
                    lista.add(parseExpression());
                }
                if (currentIndex >= tokens.size()) {
                    throw new IllegalArgumentException("Error: Falta paréntesis de cierre");
                }
                consume();
                return lista;
            } else if (token.getTipo() == TiposTokens.Numero) {
                return Integer.parseInt(token.getValor());
            } else if (token.getTipo() == TiposTokens.Identificador || token.getTipo() == TiposTokens.Operador) {
                return token.getValor();
            } else {
                throw new IllegalArgumentException("Error: Token inesperado '" + token.getValor() + "'");
            }
        }

        private Token consume() {
            return tokens.get(currentIndex++);
        }

        private Token peek() {
            if (currentIndex >= tokens.size()) {
                throw new IllegalStateException("Error: No hay más tokens para analizar");
            }
            return tokens.get(currentIndex);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese una expresión Lisp: ");
        String input = sc.nextLine();
        sc.close();

        try {
            Lexer lexer = new Lexer(input);
            List<Token> tokens = lexer.tokenizar();

            if (tokens.isEmpty()) {
                System.err.println("Error: No se pudieron generar tokens");
                return;
            }

            System.out.println("Tokens generados:");
            for (Token token : tokens) {
                System.out.println(token);
            }

            Parser parser = new Parser(tokens);
            Object resultado = parser.parse();

            if (resultado != null) {
                System.out.println("Estructura parseada:");
                System.out.println(resultado);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}