/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import java.math.BigDecimal;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import static org.parboiled.errors.ErrorUtils.printParseErrors;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author nuwan
 */
public class RestQLParser extends BaseParser<RestQLNode> {

    private static RestQLParser instance = Parboiled.createParser(RestQLParser.class);

    final Rule EQ = Terminal("=", Ch('='));
    final Rule GT = Terminal(">", AnyOf("=>"));
    final Rule NE = Terminal("<>");
    final Rule GTE = Terminal(">=");
    final Rule LT = Terminal("<", AnyOf("=<"));
    final Rule LTE = Terminal("<=");
    final Rule AND = StringIgnoreCaseWS("and").label("AND");
    final Rule IN = StringIgnoreCaseWS("in").label("IN");
    final Rule OR = StringIgnoreCaseWS("or").label("OR");
    final Rule OB = Terminal("(");
    final Rule CB = Terminal(")");
    final Rule OSB = Terminal("[");
    final Rule CSB = Terminal("]");
    final Rule COMMA = Terminal(",");
    final Rule DOT = Terminal(".");

    public Rule query() {
        return Sequence(BooleanExpression(), EOI);
    }

    @SuppressNode
    @DontLabel
    Rule Terminal(String string) {
        return Sequence(string, Spacing()).label('\'' + string + '\'');
    }

    @SuppressNode
    @DontLabel
    Rule Terminal(String string, Rule mustNotFollow) {
        return Sequence(string, TestNot(mustNotFollow), Spacing()).label('\'' + string + '\'');
    }

    Rule Spacing() {
        return ZeroOrMore(AnyOf(" \t\r\n\f").label("Whitespace"));
    }

    public Rule StringIgnoreCaseWS(String string) {
        return Sequence(IgnoreCase(string), WS());
    }

    public Rule WS() {
        return ZeroOrMore(FirstOf(COMMENT(), WS_NO_COMMENT()));
    }

    public Rule COMMENT() {
        return Sequence('#', ZeroOrMore(Sequence(TestNot(EOL()), ANY)), EOL());
    }

    public Rule WS_NO_COMMENT() {
        return FirstOf(Ch(' '), Ch('\t'), Ch('\f'), EOL());
    }

    public Rule EOL() {
        return AnyOf("\n\r");
    }

    Rule Letter() {
        // switch to this "reduced" character space version for a ~10% parser performance speedup
        //return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '_', '$');
        return FirstOf(Sequence('\\', UnicodeEscape()), new JavaLetterMatcher());
    }

    Rule UnicodeEscape() {
        return Sequence(OneOrMore('u'), HexDigit(), HexDigit(), HexDigit(), HexDigit());
    }

    @SuppressSubnodes
    @MemoMismatches
    Rule Identifier(boolean space) {
        return Sequence(Letter(), ZeroOrMore(LetterOrDigit()), Spacing());
    }
    
     @SuppressSubnodes
    @MemoMismatches
    Rule JsonIdentifier(boolean space) {
        return Sequence(Identifier(space),ZeroOrMore(Sequence(DOT,Identifier(space))));
    }

    @MemoMismatches
    Rule HexNumeral() {
        return Sequence('0', IgnoreCase('x'), OneOrMore(HexDigit()));
    }

    Rule HexDigit() {
        return FirstOf(CharRange('a', 'f'), CharRange('A', 'F'), CharRange('0', '9'));
    }

    @MemoMismatches
    Rule LetterOrDigit() {
        // switch to this "reduced" character space version for a ~10% parser performance speedup
        //return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$');
        return FirstOf(Sequence('\\', UnicodeEscape()), new JavaLetterOrDigitMatcher());
    }

    public Rule BooleanExpression() {
        return Sequence(BooleanTerm(), ZeroOrMore(OR, BooleanTerm(), push(new BinNode(pop(1, RestQLNode.class), BinNode.Op.OR, pop(RestQLNode.class)))));
    }

    Rule BooleanFactor() {
        return FirstOf(BooleanFieldExp(), Parens());
    }

    Rule BooleanTerm() {
        return Sequence(BooleanFactor(), ZeroOrMore(AND, BooleanFactor(), push(new BinNode(pop(1, RestQLNode.class), BinNode.Op.AND, pop(RestQLNode.class)))));
    }

    Rule orderByExpression() {
        return Sequence(orderBy(),push(new OrderByExpNode().add(pop(OrderByNode.class))), ZeroOrMore(Sequence(COMMA, orderBy(),push(pop(1,OrderByExpNode.class).add(pop(OrderByNode.class))))));
    }

    Rule orderBy() {
        return Sequence(JsonIdentifier(true), push(new OrderByNode(match(), OrderByNode.Order.ASC)),
                 Optional(Sequence(Terminal(":"),
                        FirstOf(
                                Sequence(StringIgnoreCaseWS("desc"), push(pop(OrderByNode.class).setOrder(OrderByNode.Order.DESC))),
                                Sequence(StringIgnoreCaseWS("asc"), push(pop(OrderByNode.class).setOrder(OrderByNode.Order.ASC)))
                        ))));
    }

    public Rule BooleanFieldExp() {
        return Sequence(JsonIdentifier(true), push(new VarNode(match())), FirstOf(
                Sequence(EQ, FieldValueExp(), push(new RelNode(pop(1, VarNode.class).getVar(), RelNode.Op.EQ, pop(ConstNode.class)))),
                Sequence(NE, FieldValueExp(), push(new RelNode(pop(1, VarNode.class).getVar(), RelNode.Op.NE, pop(ConstNode.class)))),
                Sequence(GT, FieldValueExp(), push(new RelNode(pop(1, VarNode.class).getVar(), RelNode.Op.GT, pop(ConstNode.class)))),
                Sequence(GTE, FieldValueExp(), push(new RelNode(pop(1, VarNode.class).getVar(), RelNode.Op.GTE, pop(ConstNode.class)))),
                Sequence(LT, FieldValueExp(), push(new RelNode(pop(1, VarNode.class).getVar(), RelNode.Op.LT, pop(ConstNode.class)))),
                Sequence(LTE, FieldValueExp(), push(new RelNode(pop(1, VarNode.class).getVar(), RelNode.Op.LTE, pop(ConstNode.class)))),
                Sequence(IN, arrayFieldValueExp(), push(new RelNode(pop(1, VarNode.class).getVar(), RelNode.Op.IN, pop(ConstNode.class)))),
                isNull(),
                isNotNull()
        ));
    }

    public Rule arrayFieldValueExp() {
        return FirstOf(
                arrayFieldStringValueExp(),
                arrayFieldNumberValueExp(),
                arrayFieldBooleanValueExp()
        );
    }

    public Rule arrayFieldStringValueExp() {
        return Sequence(OSB, push(new ConstStringArrayNode()), valueStringArray(), CSB);
    }

    public Rule valueStringArray() {
        return Sequence(literalS(), push(pop(1, ConstStringArrayNode.class).push(pop(ConstStringNode.class).getVal())), ZeroOrMore(Sequence(COMMA, literalS(), push(pop(1, ConstStringArrayNode.class).push(pop(ConstStringNode.class).getVal())))));
    }

    public Rule arrayFieldNumberValueExp() {
        return Sequence(OSB, push(new ConstNumberArrayNode()), valueNumberArray(), CSB);
    }

    public Rule valueNumberArray() {
        return Sequence(literalN(), push(pop(1, ConstNumberArrayNode.class).push(pop(ConstNumberNode.class).getVal())), ZeroOrMore(Sequence(COMMA, literalN(), push(pop(1, ConstNumberArrayNode.class).push(pop(ConstNumberNode.class).getVal())))));
    }

    public Rule arrayFieldBooleanValueExp() {
        return Sequence(OSB, push(new ConstBooleanArrayNode()), valueBooleanArray(), CSB);
    }

    public Rule valueBooleanArray() {
        return Sequence(literalB(), push(pop(1, ConstBooleanArrayNode.class).push(pop(ConstBooleanNode.class).getVal())), ZeroOrMore(Sequence(COMMA, literalB(), push(pop(1, ConstBooleanArrayNode.class).push(pop(ConstBooleanNode.class).getVal())))));
    }

    public Rule FieldValueExp() {
        return literal();
    }

    public Rule isNull() {
        return Sequence(StringIgnoreCaseWS("is"), StringIgnoreCaseWS("null"), push(new FieldCheckNode(pop(VarNode.class).getVar(), false))).suppressSubnodes().label("is null");
    }

    public Rule isNotNull() {
        return Sequence(StringIgnoreCaseWS("is"), StringIgnoreCaseWS("not"), StringIgnoreCaseWS("null"),
                push(new FieldCheckNode(pop(VarNode.class).getVar(), true))).suppressSubnodes().label("is not null");
    }

    <T> T pop(Class<T> cls) {
        return cls.cast(super.pop());
    }

    <T> T pop(int index, Class<T> cls) {
        return cls.cast(super.pop(index));
    }

    public Rule Parens() {
        return Sequence(OB, BooleanExpression(), CB);
    }

    Rule literalS() {
        return Sequence(
                FirstOf(
                        Sequence(CharLiteral(), push(new ConstStringNode(match()))),
                        Sequence(StringLiteral(), push(new ConstStringNode(match())))
                ),
                Spacing()
        );
    }

    Rule literalN() {
        return Sequence(
                FirstOf(
                        Sequence(FloatLiteral(), push(new ConstNumberNode(new BigDecimal(match())))),
                        Sequence(IntegerLiteral(), push(new ConstNumberNode(new BigDecimal(match()))))
                ),
                Spacing()
        );
    }

    Rule literalB() {
        return Sequence(
                FirstOf(
                        Sequence("true", TestNot(LetterOrDigit()), push(new ConstBooleanNode(Boolean.valueOf(match())))),
                        Sequence("false", TestNot(LetterOrDigit()), push(new ConstBooleanNode(Boolean.valueOf(match()))))
                ),
                Spacing()
        );
    }

    Rule literal() {
        return Sequence(
                FirstOf(
                        Sequence(FloatLiteral(), push(new ConstNumberNode(new BigDecimal(match())))),
                        Sequence(IntegerLiteral(), push(new ConstNumberNode(new BigDecimal(match())))),
                        Sequence(CharLiteral(), push(new ConstStringNode(match()))),
                        Sequence(StringLiteral(), push(new ConstStringNode(match()))),
                        Sequence("true", TestNot(LetterOrDigit()), push(new ConstBooleanNode(Boolean.valueOf(match())))),
                        Sequence("false", TestNot(LetterOrDigit()), push(new ConstBooleanNode(Boolean.valueOf(match())))),
                        Sequence("null", TestNot(LetterOrDigit()), push(new ConstStringNode(match())))
                ),
                Spacing()
        );
    }

    Rule StringLiteral() {
        return FirstOf(Sequence(
                '"',
                ZeroOrMore(
                        FirstOf(
                                Escape(),
                                Sequence(TestNot(AnyOf("\r\n\"\\")), ANY)
                        )
                ).suppressSubnodes(),
                '"'
        ), Sequence(
                '\'',
                ZeroOrMore(
                        FirstOf(
                                Escape(),
                                Sequence(TestNot(AnyOf("\r\n\'\\")), ANY)
                        )
                ).suppressSubnodes(),
                '\''
        ));
    }

    Rule CharLiteral() {
        return Sequence(
                '\'',
                FirstOf(Escape(), Sequence(TestNot(AnyOf("'\\")), ANY)).suppressSubnodes(),
                '\''
        );
    }

    Rule Escape() {
        return Sequence('\\', FirstOf(AnyOf("btnfr\"\'\\"), OctalEscape(), UnicodeEscape()));
    }

    Rule OctalEscape() {
        return FirstOf(
                Sequence(CharRange('0', '3'), CharRange('0', '7'), CharRange('0', '7')),
                Sequence(CharRange('0', '7'), CharRange('0', '7')),
                CharRange('0', '7')
        );
    }

    Rule FloatLiteral() {
        return FirstOf(HexFloat(), DecimalFloat());
    }

    @SuppressSubnodes
    Rule IntegerLiteral() {
        return Sequence(FirstOf(HexNumeral(), OctalNumeral(), DecimalNumeral()), Optional(AnyOf("lL")));
    }

    @SuppressSubnodes
    Rule OctalNumeral() {
        return Sequence('0', OneOrMore(CharRange('0', '7')));
    }

    @SuppressSubnodes
    Rule DecimalNumeral() {
        return FirstOf('0', Sequence(CharRange('1', '9'), ZeroOrMore(Digit())));
    }

    @SuppressSubnodes
    Rule DecimalFloat() {
        return FirstOf(
                Sequence(OneOrMore(Digit()), '.', ZeroOrMore(Digit()), Optional(Exponent()), Optional(AnyOf("fFdD"))),
                Sequence('.', OneOrMore(Digit()), Optional(Exponent()), Optional(AnyOf("fFdD"))),
                Sequence(OneOrMore(Digit()), Exponent(), Optional(AnyOf("fFdD"))),
                Sequence(OneOrMore(Digit()), Optional(Exponent()), AnyOf("fFdD"))
        );
    }

    @SuppressSubnodes
    Rule HexFloat() {
        return Sequence(HexSignificant(), BinaryExponent(), Optional(AnyOf("fFdD")));
    }

    Rule HexSignificant() {
        return FirstOf(
                Sequence(FirstOf("0x", "0X"), ZeroOrMore(HexDigit()), '.', OneOrMore(HexDigit())),
                Sequence(HexNumeral(), Optional('.'))
        );
    }

    Rule BinaryExponent() {
        return Sequence(AnyOf("pP"), Optional(AnyOf("+-")), OneOrMore(Digit()));
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    Rule Exponent() {
        return Sequence(AnyOf("eE"), Optional(AnyOf("+-")), OneOrMore(Digit()));
    }

    public static RestQLNode parse(String rql) {
        ParsingResult<?> result = new ReportingParseRunner(instance.query()).run(rql);

        if (result.hasErrors()) {
            throw new RestQLException(printParseErrors(result));
        }
        RestQLNode node = (RestQLNode) result.resultValue;
        return node;
    }

    public static OrderByExpNode parseOrderBy(String rql) {
        ParsingResult<?> result = new ReportingParseRunner(instance.orderByExpression()).run(rql);

        if (result.hasErrors()) {
            throw new RestQLException(printParseErrors(result));
        }
        OrderByExpNode node = (OrderByExpNode) result.resultValue;
        return node;
    }

    public static void main(String[] args) {
//
        RestQLNode node = RestQLParser.parse("name.safsa = 'nuwan' or  age = 30 and (st > 'abc' or country = 'LK') and xx in [309,23.5]");
//        System.out.println("gson : " + GsonCodec.encode(node));
//        System.out.println("json : " + node.toJson());
//
//        RestQLNode node2 = RestQLNode.fromJson(node.toJson());
        SqlNode sql = new SqlNode();
        String out = sql.eval(node);
        System.out.println(out);
//        
//        OrderByExpNode orderBy = RestQLParser.parseOrderBy("name:desc,age:asc");
//        OrderByExpNode orderBy2 = RestQLNode.fromJson(orderBy.toJson());
//        System.out.println(orderBy2.toJson());
////        //RestQLParser parser = Parboiled.createParser(RestQLParser.class);
////        long rate = 0;
////        long s = System.currentTimeMillis();
////        while (true) {
//////            ParsingResult<?> result = new ReportingParseRunner(parser.query()).run("name = 'nuwan' and (age = 30)");
//////
//////            if (result.hasErrors()) {
//////                throw new RestQLException(printParseErrors(result));
//////            }
////             RestQLParser.parse("name = 'nuwan' and (age = 30)");
////            rate++;
////            if (System.currentTimeMillis() - s >= 1000) {
////                System.out.println("rate: " + rate);
////                rate = 0;
////                s = System.currentTimeMillis();
////
////            }
////        }
//
    }
}
