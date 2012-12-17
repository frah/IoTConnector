package iotc.parser;

import org.codehaus.jparsec.*;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Unary;

/**
 * 統計関数文字列パーサ
 * @author atsushi-o
 */
public final class StatisticalMethodParser {
    enum BinaryOperator implements Binary<Double> {
        PLUS {
            @Override public Double map(Double a, Double b) {
                return a + b;
            }
        },
        MINUS {
            @Override public Double map(Double a, Double b) {
                return a - b;
            }
        },
        MUL {
            @Override public Double map(Double a, Double b) {
                return a * b;
            }
        },
        DIV {
            @Override public Double map(Double a, Double b) {
                return a / b;
            }
        }
    }
    enum UnaryOperator implements Unary<Double> {
        NEG {
            @Override public Double map(Double n) {
                return -n;
            }
        }
    }
    static final Parser<Double> NUMBER = Terminals.DecimalLiteral.PARSER.map(new Map<String, Double>() {
        @Override public Double map(String s) {
            return Double.valueOf(s);
        }
    });

    private static final Terminals OPERATORS = Terminals.operators("+", "-", "*", "/", "(", ")");

    static final Parser<Void> IGNORED =
            Parsers.or(Scanners.JAVA_LINE_COMMENT, Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES).skipMany();
    static final Parser<?> TOKENIZER =
            Parsers.or(Terminals.DecimalLiteral.TOKENIZER, OPERATORS.tokenizer());

    static Parser<?> term(String... names) {
        return OPERATORS.token(names);
    }

    static final Parser<BinaryOperator> WHITESPACE_MUL =
            term("+", "-", "*", "/").not().retn(BinaryOperator.MUL);

    static <T> Parser<T> op(String name, T value) {
        return term(name).retn(value);
    }

    static Parser<Double> calc(Parser<Double> atom) {
        Parser.Reference<Double> ref = Parser.newReference();
        Parser<Double> unit = ref.lazy().between(term("("), term(")")).or(atom);
        Parser<Double> parser = new OperatorTable<Double>()
                .infixl(op("+", BinaryOperator.PLUS), 10)
                .infixl(op("-", BinaryOperator.MINUS), 10)
                .infixl(op("*", BinaryOperator.MUL).or(WHITESPACE_MUL), 20)
                .infixl(op("/", BinaryOperator.DIV), 20)
                .prefix(op("-", UnaryOperator.NEG), 30)
                .build(unit);
        ref.set(parser);
        return parser;
    }

    private static final Parser<Double> PARSER = calc(NUMBER).from(TOKENIZER, IGNORED);
    /**
     * 統計関数文字列をパースし，結果値を返す
     * @param source 統計関数文字列
     * @return 計算結果
     */
    public static Double parse(CharSequence source) {
        return PARSER.parse(source);
    }
    /* インスタンス化抑制 */
    private StatisticalMethodParser() {}

    public static void main(String[] args) {
        String a = "(12+3)*4";
        System.out.println(a+" = "+StatisticalMethodParser.parse(a));
    }
}
