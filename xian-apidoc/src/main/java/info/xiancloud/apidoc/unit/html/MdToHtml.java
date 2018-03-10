package info.xiancloud.apidoc.unit.html;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * markdown to html
 *
 * @author happyyangyuan
 */
public class MdToHtml {

    /**
     * @param md the markdown string.
     * @return converted html
     */
    public static String mdToHtml(String md) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(md);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
