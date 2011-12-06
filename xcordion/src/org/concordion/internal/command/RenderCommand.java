package org.concordion.internal.command;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Predicate;
import org.concordion.api.Renderer;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.CommandCall;
import org.concordion.internal.util.Check;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

public class RenderCommand extends AbstractCommand {

    private final Map<Predicate, Renderer> renderers;

    public RenderCommand(Map<Predicate, Renderer> renderers) {
        this.renderers = renderers;
    }

    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'render' is not supported");

        Object result = render(evaluator.evaluate(commandCall.getExpression()));

        Element desiredParent = commandCall.getElement();
        desiredParent.removeChildren();
        if (result != null) {
            try {
                Builder parser = new Builder();
                Document doc = parser.build(new StringReader(result.toString()));

                desiredParent.appendChild(new Element(new nu.xom.Element(doc.getRootElement())));
            } catch (ParsingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Element child = new Element("i");
            child.appendText("(null)");
            desiredParent.appendChild(child);
        }
    }

    private Object render(Object result) {
        for (Map.Entry<Predicate, Renderer> entry : renderers.entrySet()) {
            if (entry.getKey().matches(result)) {
                result = entry.getValue().render(result);
            }
        }
        return result;
    }

}
