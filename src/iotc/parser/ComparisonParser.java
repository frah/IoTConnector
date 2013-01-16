package iotc.parser;

import org.codehaus.jparsec.*;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.functors.Unary;

/**
 * Created with IntelliJ IDEA.
 * User: atsushi-o
 * Date: 12/12/23
 * Time: 0:58
 * To change this template use File | Settings | File Templates.
 */
public final class ComparisonParser {
    enum BoolOperator implements Binary<Boolean> {
        AND {
            @Override public Boolean map(Boolean a, Boolean b) {
                return a && b;
            }
        },
        OR {
            @Override public Boolean map(Boolean a, Boolean b) {
                return a || b;
            }
        },
        XOR {
            @Override public Boolean map(Boolean a, Boolean b) {
                return a ^ b;
            }
        },
    }
    enum UnaryOperator implements Unary<Boolean> {
        NOT {
            @Override public Boolean map(Boolean n) {
                return !n;
            }
        }
    }
    enum ComparisonOperator implements Map2<Double, Double, Boolean> {
        EQ {
            @Override public Boolean map(Double a, Double b) {
                return a == b;
            }
        },
        NEQ {
            @Override public Boolean map(Double a, Double b) {
                return a != b;
            }
        },
        GT {
            @Override public Boolean map(Double a, Double b) {
                return a > b;
            }
        },
        LT {
            @Override public Boolean map(Double a, Double b) {
                return a < b;
            }
        },
        GTE {
            @Override public Boolean map(Double a, Double b) {
                return a >= b;
            }
        },
        LTE {
            @Override public Boolean map(Double a, Double b) {
                return a <= b;
            }
        }
    }

    static final Parser<Double> NUMBER = Terminals.DecimalLiteral.PARSER.map(new Map<String, Double>(){
        @Override public Double map(String s) {
            return Double.valueOf(s);
        }
    });

    private static final Terminals OPERATORS =
            Terminals.operators("==", "!=", ">", "<", ">=", "<=", "&&", "||", "!");
    static final Parser<Void> IGNORED = Parsers.or(Scanners.WHITESPACES).skipMany();
    static final Parser<?> TOKENIZER =
            Parsers.or(Terminals.DecimalLiteral.TOKENIZER, OPERATORS.tokenizer());

    static Parser<?> term(String... names) {
        return OPERATORS.token(names);
    }
    static <T> Parser<T> op(String name, T value) {
        return term(name).retn(value);
    }

    static Parser<Boolean> comparison(Parser<Boolean> atom) {
        Parser.Reference<Boolean> ref = Parser.newReference();

        Parser<Boolean> parser = new OperatorTable<Boolean>()
                .infixl(op("||", BoolOperator.OR), 10)
                .infixl(op("&&", BoolOperator.AND), 20)
                .infixl(op("^", BoolOperator.XOR), 30)
                .prefix(op("!", UnaryOperator.NOT), 50)
                .build(atom);
        return parser;
    }
}
