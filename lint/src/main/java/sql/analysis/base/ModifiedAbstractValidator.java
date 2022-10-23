package sql.analysis.base;

import sql.analysis.base.select.ModifiedExpressionValidator;
import sql.analysis.base.select.ModifiedItemsListValidator;
import sql.analysis.base.select.ModifiedOrderByValidator;
import sql.analysis.base.select.ModifiedSelectValidator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.ValidationContext;
import net.sf.jsqlparser.util.validation.ValidationException;
import net.sf.jsqlparser.util.validation.Validator;
import net.sf.jsqlparser.util.validation.feature.FeatureContext;
import net.sf.jsqlparser.util.validation.feature.FeatureSetValidation;
import net.sf.jsqlparser.util.validation.metadata.DatabaseMetaDataValidation;
import net.sf.jsqlparser.util.validation.metadata.MetadataContext;
import net.sf.jsqlparser.util.validation.metadata.Named;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author qwefgh90
 * It was cloned from net/sf/jsqlparser/util/validation/validator/AbstractValidator.java
 * It adds new methods passing ASTNodeAccess as an argument to capabilities.
 */
public abstract class ModifiedAbstractValidator<S> implements Validator<S> {

    private ValidationContext context = new ValidationContext();

    private Map<ValidationCapability, Set<ValidationException>> errors = new HashMap<>();

    private Map<Class<? extends ModifiedAbstractValidator<?>>, ModifiedAbstractValidator<?>> validatorForwards = new HashMap<>();

    public <T extends ModifiedAbstractValidator<?>> T getValidator(Class<T> type) {
        return type.cast(validatorForwards.computeIfAbsent(type, this::newObject));
    }

    private <E extends Validator<?>> E newObject(Class<E> type) {
        try {
            E e = type.cast(type.getConstructor().newInstance());
            e.setContext(context());
            return e;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Type " + type + " cannot be constructed by empty constructor!");
        }
    }

    protected Consumer<ValidationException> getMessageConsumer(ValidationCapability c) {
        return s -> putError(c, s);
    }

    protected ValidationContext context() {
        return context(true);
    }

    protected ValidationContext context(boolean reInit) {
        return context.reinit(reInit);
    }

    /**
     * adds an error for this {@link ValidationCapability}
     *
     * @param capability
     * @param error
     */
    protected void putError(ValidationCapability capability, ValidationException error) {
        errors.computeIfAbsent(capability, k -> new HashSet<>()).add(error);
    }

    @Override
    public final Map<ValidationCapability, Set<ValidationException>> getValidationErrors() {
        Map<ValidationCapability, Set<ValidationException>> map = new HashMap<>();
        map.putAll(errors);
        for (ModifiedAbstractValidator<?> v : validatorForwards.values()) {
            for (Map.Entry<ValidationCapability, Set<ValidationException>> e : v.getValidationErrors().entrySet()) {
                Set<ValidationException> set = map.get(e.getKey());
                if (set == null) {
                    map.put(e.getKey(), e.getValue());
                } else {
                    set.addAll(e.getValue());
                }
            }
        }
        return map;
    }

    public Collection<ValidationCapability> getCapabilities() {
        return context().getCapabilities();
    }

    @Override
    public final void setContext(ValidationContext context) {
        this.context = context;
    }

    protected <E> void validateOptional(E element, Consumer<E> elementConsumer) {
        if (element != null) {
            elementConsumer.accept(element);
        }
    }

    protected <E, V extends Validator<?>> void validateOptionalList(
            List<E> elementList, Supplier<V> validatorSupplier, BiConsumer<E, V> elementConsumer) {
        if (isNotEmpty(elementList)) {
            V validator = validatorSupplier.get();
            elementList.forEach(e -> elementConsumer.accept(e, validator));
        }
    }

    /**
     * a multi-expression in clause: {@code ((a, b), (c, d))}
     */
    protected void validateOptionalMultiExpressionList(MultiExpressionList multiExprList) {
        if (multiExprList != null) {
            ModifiedExpressionValidator v = getValidator(ModifiedExpressionValidator.class);
            multiExprList.getExpressionLists().stream().map(ExpressionList::getExpressions).flatMap(List::stream)
                    .forEach(e -> e.accept(v));
        }
    }

    protected void validateOptionalExpression(Expression expression) {
        validateOptional(expression, e -> e.accept(getValidator(ModifiedExpressionValidator.class)));
    }

    protected void validateOptionalExpression(Expression expression, ModifiedExpressionValidator v) {
        validateOptional(expression, e -> e.accept(v));
    }

    protected void validateOptionalExpressions(List<? extends Expression> expressions) {
        validateOptionalList(expressions, () -> getValidator(ModifiedExpressionValidator.class), (o, v) -> o.accept(v));
    }

    protected void validateOptionalFromItems(FromItem... fromItems) {
        validateOptionalFromItems(Arrays.asList(fromItems));
    }

    protected void validateOptionalFromItems(List<? extends FromItem> fromItems) {
        validateOptionalList(fromItems, () -> getValidator(ModifiedSelectValidator.class),
                this::validateOptionalFromItem);
    }

    protected void validateOptionalOrderByElements(List<OrderByElement> orderByElements) {
        validateOptionalList(orderByElements, () -> getValidator(ModifiedOrderByValidator.class), (o, v) -> o.accept(v));
    }

    protected void validateOptionalFromItem(FromItem fromItem) {
        validateOptional(fromItem, i -> i.accept(getValidator(ModifiedSelectValidator.class)));
    }

    protected void validateOptionalFromItem(FromItem fromItem, ModifiedSelectValidator v) {
        validateOptional(fromItem, i -> i.accept(v));
    }

    protected void validateOptionalItemsList(ItemsList itemsList) {
        validateOptional(itemsList, i -> i.accept(getValidator(ModifiedItemsListValidator.class)));
    }

    /**
     * Iterates through all {@link ValidationCapability} and validates the feature
     * with {@link #validateFeature(ValidationCapability, Feature)}
     *
     * @param feature
     */
    public void validateFeature(Feature feature) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, feature);
        }
    }

    /**
     * Iterates through all {@link ValidationCapability} and validates
     * <ul>
     * <li>the feature with
     * {@link #validateFeature(ValidationCapability, Feature)}</li>
     * </ul>
     *
     * @param feature
     * @param namedObject
     * @param fqn - fully qualified name of named object
     */
    protected void validateFeatureAndName(Feature feature, ASTNodeAccess simpleNode, NamedObject namedObject, String fqn) {
        validateFeatureAndNameWithAlias(feature, simpleNode, namedObject, fqn, null);
    }

    /**
     * Iterates through all {@link ValidationCapability} and validates
     * <ul>
     * <li>the feature with
     * {@link #validateFeature(ValidationCapability, Feature)}</li>
     * </ul>
     *
     * @param feature
     * @param namedObject
     * @param fqn - fully qualified name of named object
     * @param alias
     */
    protected void validateFeatureAndNameWithAlias(Feature feature, ASTNodeAccess simpleNode, NamedObject namedObject, String fqn, String alias) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, feature);
            validateNameWithAlias(c, simpleNode, namedObject, fqn, alias, true);
        }
    }

    /**
     * Iterates through all {@link ValidationCapability} and validates for the name
     *
     * @param namedObject
     * @param fqn - fully qualified name of named object
     */
    protected void validateName(ASTNodeAccess simpleNode, NamedObject namedObject, String fqn) {
        validateNameWithAlias(simpleNode, namedObject, fqn, null);
    }

    /**
     * Iterates through all {@link ValidationCapability} and validates for the name
     *
     * @param namedObject
     * @param fqn - fully qualified name of named object
     * @param alias
     */
    protected void validateNameWithAlias(ASTNodeAccess simpleNode, NamedObject namedObject, String fqn, String alias) {
        for (ValidationCapability c : getCapabilities()) {
            validateNameWithAlias(c, simpleNode, namedObject, fqn, alias, true);
        }
    }

    /**
     * Validates the feature if given {@link ValidationCapability} is a
     * {@link FeatureSetValidation} and condition is <code>true</code>
     *
     * @param capability
     * @param condition
     * @param feature
     */
    protected void validateFeature(ValidationCapability capability, boolean condition, Feature feature) {
        if (condition) {
            validateFeature(capability, feature);
        }
    }

    /**
     * validates for the feature if given elements is not empty - see
     * {@link #isNotEmpty(Collection)}
     *
     * @param capability
     * @param elements
     * @param feature
     */
    protected void validateOptionalFeature(ValidationCapability capability, List<?> elements, Feature feature) {
        validateFeature(capability, isNotEmpty(elements), feature);
    }

    /**
     * Validates for the feature if given element is not <code>null</code>
     *
     * @param capability
     * @param element
     * @param feature
     */
    protected void validateOptionalFeature(ValidationCapability capability, Object element, Feature feature) {
        validateFeature(capability, element != null, feature);
    }

    /**
     * Validates if given {@link ValidationCapability} is a
     * {@link FeatureSetValidation}
     *
     * @param capability
     * @param feature
     */
    protected void validateFeature(ValidationCapability capability, Feature feature) {
        if (capability instanceof FeatureSetValidation) {
            capability.validate(context().put(FeatureContext.feature, feature),
                    getMessageConsumer(capability));
        }
    }

    /**
     * Validates if given {@link ValidationCapability} is a
     * {@link DatabaseMetaDataValidation}
     *
     * @param capability
     * @param namedObject
     * @param fqn - fully qualified name of named object
     * @param alias
     */
    protected void validateNameWithAlias(ValidationCapability capability, ASTNodeAccess simpleNode, NamedObject namedObject, String fqn, String alias) {
        validateNameWithAlias(capability, simpleNode, namedObject, fqn, alias, true);
    }

    /**
     * @param capability
     * @param namedObject
     * @param fqn - fully qualified name of named object
     */
    protected void validateName(ValidationCapability capability, ASTNodeAccess simpleNode, NamedObject namedObject, String fqn) {
        validateNameWithAlias(capability, simpleNode, namedObject, fqn, null, true);
    }

    /**
     * Validates if given {@link ValidationCapability} is a
     * {@link DatabaseMetaDataValidation}
     *
     * @param capability
     * @param namedObject
     * @param fqn - fully qualified name of named object
     * @param alias
     * @param exists      - <code>true</code>, check for existence,
     *                    <code>false</code>, check for non-existence
     */
    protected void validateNameWithAlias(ValidationCapability capability, ASTNodeAccess simpleNode, NamedObject namedObject, String fqn, String alias,
                                         boolean exists,
                                         NamedObject... parents) {
        if (capability instanceof DatabaseMetaDataValidation) {
            capability.validate(context()
                            .put(MetadataContext.named, createNamed(namedObject, fqn, alias, parents)) //
                            .put(MetadataContext.exists, exists)
                            .put(AdditionalContextKey.SimpleNode, simpleNode),
                    getMessageConsumer(capability));
        }else{
            capability.validate(context()
                            .put(MetadataContext.named, createNamed(namedObject, fqn, alias, parents)) //
                            .put(MetadataContext.exists, exists)
                            .put(AdditionalContextKey.SimpleNode, simpleNode),
                    getMessageConsumer(capability));
        }
    }

    /**
     * @param capability
     * @param namedObject
     * @param fqn - fully qualified name of named object
     * @param exists
     * @param parents
     */
    protected void validateName(ValidationCapability capability, ASTNodeAccess simpleNode, NamedObject namedObject, String fqn, boolean exists,
                                NamedObject... parents) {
        validateNameWithAlias(capability, simpleNode, namedObject, fqn, null, exists, parents);
    }

    /**
     * @param capability
     * @param name
     */
    protected void validateOptionalColumnName(ValidationCapability capability, ASTNodeAccess simpleNode, String name) {
        validateOptionalName(capability, simpleNode, NamedObject.column, name, null, true);
    }

    /**
     * @param capability
     * @param name
     * @param alias
     */
    protected void validateOptionalColumnNameWithAlias(ValidationCapability capability, ASTNodeAccess simpleNode, String name, String alias) {
        validateOptionalName(capability, simpleNode, NamedObject.column, name, alias, true);
    }

    /**
     * @param capability
     * @param namedObject
     * @param name
     * @param alias
     * @param parents
     */
    protected void validateOptionalNameWithAlias(ValidationCapability capability, ASTNodeAccess simpleNode, NamedObject namedObject, String name,
                                                 String alias,
                                                 NamedObject... parents) {
        validateOptionalName(capability, simpleNode, namedObject, name, alias, true, parents);
    }

    /**
     * @param capability
     * @param namedObject
     * @param name
     * @param parents
     */
    protected void validateOptionalName(ValidationCapability capability, ASTNodeAccess simpleNode, NamedObject namedObject, String name,
                                        NamedObject... parents) {
        validateOptionalNameWithAlias(capability, simpleNode, namedObject, name, (String) null, parents);
    }

    /**
     * @param capability
     * @param namedObject
     * @param name
     * @param alias
     * @param exists
     * @param parents
     */
    protected void validateOptionalName(ValidationCapability capability, ASTNodeAccess simpleNode, NamedObject namedObject, String name,
                                        String alias,
                                        boolean exists,
                                        NamedObject... parents) {
        if (name != null) {
            validateNameWithAlias(capability, simpleNode, namedObject, name, alias, exists, parents);
        }
    }

    protected boolean isNotEmpty(Collection<?> c) {
        return c != null && !c.isEmpty();
    }

    protected boolean isNotEmpty(String c) {
        return c != null && !c.isEmpty();
    }

    public static Named createNamed(NamedObject namedObject, String fqn, String alias,
    NamedObject... parents) {
        return new Named(namedObject, fqn).setAlias(alias).setParents(Arrays.asList(parents));
    }

}
