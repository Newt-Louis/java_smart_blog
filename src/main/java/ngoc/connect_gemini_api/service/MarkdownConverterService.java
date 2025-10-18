package ngoc.connect_gemini_api.service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Service;

@Service
public class MarkdownConverterService {
    // Khởi tạo Parser và HtmlRenderer một lần và tái sử dụng.
    // Chúng là thread-safe (an toàn trong môi trường đa luồng).
    private final Parser parser;
    private final HtmlRenderer renderer;
    private final PolicyFactory htmlPolicy;

    public MarkdownConverterService() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
        this.htmlPolicy = Sanitizers.BLOCKS
                .and(Sanitizers.FORMATTING)
                .and(Sanitizers.IMAGES)
                .and(Sanitizers.LINKS)
                .and(Sanitizers.TABLES)
                .and(Sanitizers.STYLES);
    }

    /**
     * Chuyển đổi một chuỗi Markdown thành chuỗi HTML.
     *
     * @param markdownContent Nội dung dạng Markdown cần chuyển đổi.
     * @return Nội dung đã được chuyển đổi sang định dạng HTML.
     */
    public String markdownToHtml(String markdownContent) {
        if (markdownContent == null || markdownContent.isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdownContent);
        String rawHtml = renderer.render(document);
        return htmlPolicy.sanitize(rawHtml);
    }
}
