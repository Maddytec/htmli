package com.maddytec.htmli.exceptions.validations;

import com.maddytec.htmli.exceptions.InputViolationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class NoHtmlValidator implements ConstraintValidator<NoHtml, String> {

    private static final PolicyFactory POLICY_FACTORY = new HtmlPolicyBuilder().toFactory();
    private String annotationMessage;

    @Override
    public void initialize(NoHtml constraintAnnotation) {
        annotationMessage = constraintAnnotation.message();
    }

    public String clearXss(String value) {
        var s = POLICY_FACTORY.sanitize(value);
//    if we only allow basic html tags
//        return Jsoup.clean(s, Safelist.basic());

//    allow selected tags and attributes
//        return Jsoup.clean(s, Safelist.relaxed().addAttributes("p", "b"));

//    if we do not want any html tag
        return Jsoup.clean(s, Safelist.none());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        var valueSanitized = clearXss(value);

        if (valueSanitized.equals(value)) {
            return true;
        }

        var contexts =
                ((ConstraintValidatorContextImpl) constraintValidatorContext)
                        .getConstraintViolationCreationContexts();
        var field = contexts.getFirst().getPath().getLeafNode().asString();
        throw new InputViolationException(field + ": " + annotationMessage);
    }

}
