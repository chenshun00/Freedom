package alchemystar.freedom.sql.select.item;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLTextLiteralExpr;

import alchemystar.hero.meta.value.Value;
import alchemystar.hero.meta.value.ValueInt;
import alchemystar.hero.meta.value.ValueLong;
import alchemystar.hero.meta.value.ValueString;
import alchemystar.hero.sql.parser.SelectVisitor;
import alchemystar.hero.sql.select.TableFilter;

/**
 * @Author lizhuyang
 */
public class SelectExprEval {

    private SQLExpr expr;

    private SelectVisitor selectVisitor;

    public SelectExprEval(SQLExpr expr, SelectVisitor selectVisitor) {
        this.expr = expr;
        this.selectVisitor = selectVisitor;
    }

    public Value eval() {
        return eval(expr);
    }

    public Value eval(SQLExpr x) {
        if (x instanceof SQLBinaryOpExpr) {
            return evalBinaryOp((SQLBinaryOpExpr) x);
        }
        if (x instanceof SQLPropertyExpr) {
            return evalPropertyExpr((SQLPropertyExpr) x);
        }
        if (x instanceof SQLIdentifierExpr) {
            return evalIdentifierExpr((SQLIdentifierExpr) x);
        }
        if (x instanceof SQLTextLiteralExpr) {
            return new ValueString(((SQLTextLiteralExpr) x).getText());
        }
        if (x instanceof SQLNumericLiteralExpr) {
            return new ValueInt(((SQLNumericLiteralExpr) x).getNumber().intValue());
        }
        throw new RuntimeException("not support this expr , expr=" + x.toString());
    }

    // 四则运算
    public Value evalBinaryOp(SQLBinaryOpExpr x) {
        Value leftValue = eval(x.getLeft());
        Value rightValue = eval(x.getRight());
        switch (x.getOperator()) {
            case Add:
                return leftValue.add(rightValue);
            case Subtract:
                return leftValue.subtract(rightValue);
            case Multiply:
                return leftValue.multiply(rightValue);
            case Divide:
                return leftValue.divide(rightValue);
            case Concat:
                return leftValue.concat(rightValue);
            case Equality:
                return leftValue.equality(rightValue);
            case GreaterThan:
                return leftValue.greaterThan(rightValue);
            case GreaterThanOrEqual:
                return leftValue.greaterThanOrEqual(rightValue);
            case LessThan:
                return leftValue.lessThan(rightValue);
            case LessThanOrEqual:
                return leftValue.lessThanOrEqual(rightValue);
            case BooleanAnd:
                return leftValue.and(rightValue);
            case BooleanOr:
                return leftValue.or(rightValue);
            default:
                throw new RuntimeException("not support this op [" + x.getOperator().getName() + "]");
        }
    }

    public Value evalPropertyExpr(SQLPropertyExpr expr) {
        String alias = expr.getOwner().toString();
        TableFilter tableFilter = selectVisitor.getTableFilter(alias);
        String columnName = expr.getSimpleName().toLowerCase();
        return tableFilter.getValue(columnName);
    }

    // 如果没有table alias,则走此逻辑
    public Value evalIdentifierExpr(SQLIdentifierExpr x) {
        return selectVisitor.getTableFilter().getValue(x.getLowerName());
    }

}
