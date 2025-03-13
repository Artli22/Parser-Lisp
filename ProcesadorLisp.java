import java.util.*;

public class ProcesadorLisp {

    // Tipos posibles de Tokens
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

    // Clase encargada en el proceso de Lexer
    static class Lexer {
        private final String expresion;

        public Lexer(String expresion) {
            this.expresion = expresion;
        }
        // Metodo para comprobar el numero de parentesis
        public List<Token> tokenizar() {
            if (!isBalanced(expresion)) {
                throw new IllegalArgumentException("La expresión tiene paréntesis desbalanceados");
            }

            List<Token> tokens = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();
            char[] caracteres = expresion.toCharArray();

            // Clasificador de tipo de token
            for (int i = 0; i < caracteres.length; i++) {
                char c = caracteres[i];

                if (Character.isWhitespace(c)) continue;

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

    // Clase encargada en el proceso Parser
    static class Parser {
        // Ingreso de expresion Lisp al Parser
        private final List<Token> tokens;
        private int currentIndex = 0;

        public Parser(List<Token> tokens) {
            this.tokens = tokens;
        }

        public Object parse() {
            return parseExpression();
        }

        private Object parseExpression() {
            Token token = consume();

            // Agrega el analisis de una lista interna dentro de la expresion que esta dentro de () interiores
            if (token.getTipo() == TiposTokens.Parentesis_Abierto) {
                List<Object> lista = new ArrayList<>();
                while (peek().getTipo() != TiposTokens.Parentesis_Cerrado) {
                    lista.add(parseExpression());
                }
                consume(); 
                return lista;
                // Retornar valores int
            } else if (token.getTipo() == TiposTokens.Numero) {
                return Integer.parseInt(token.getValor());
                // Retornar valores String
            } else if (token.getTipo() == TiposTokens.Identificador || token.getTipo() == TiposTokens.Operador) {
                return token.getValor();
            } else {
                return null; 
            }
        }
        // Retorna el siguiente valor y sigue con el siguiente valor de la expresion
        private Token consume() {
            return tokens.get(currentIndex++);
        }

        // Retorna el siguiente valor
        private Token peek() {
            return tokens.get(currentIndex);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese una expresión Lisp: ");
        String input = sc.nextLine();

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenizar();

        System.out.println("Tokens generados:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        Object resultado = parser.parse();

        System.out.println("Estructura parseada:");
        System.out.println(resultado);
    }
}
