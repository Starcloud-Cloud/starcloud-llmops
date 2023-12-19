package com.starcloud.ops.business.app.util;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import lombok.Getter;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("unused")
public class MarkdownUtils {

    /**
     * markdown parser
     */
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();

    /**
     * html renderer
     */
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    /**
     * text renderer
     */
    private static final TextContentRenderer TEXT_CONTENT_RENDERER = TextContentRenderer.builder().build();


    /**
     * markdown to html
     *
     * @param markdown markdown
     * @return html
     */
    public static String convertToHtml(String markdown) {
        return HTML_RENDERER.render(MARKDOWN_PARSER.parse(markdown));
    }


    /**
     * markdown to text
     */
    public static String convertToText(String markdown) {
        return TEXT_CONTENT_RENDERER.render(MARKDOWN_PARSER.parse(markdown));
    }

    /**
     * 获取指定级别的标题
     *
     * @param market markdown
     * @param level  级别
     * @return 段落
     */
    public static List<Paragraph> getHeadingsByLevel(String market, Level level) {
        Node node = MARKDOWN_PARSER.parse(market);
        return getHeadingsByLevel(node, level);
    }

    /**
     * 获取指定级别的标题
     *
     * @param node  node
     * @param level level
     * @return List<Node>
     */
    public static List<Paragraph> getHeadingsByLevel(Node node, Level level) {
        List<Paragraph> secondLevelHeadings = new ArrayList<>();

        if (node instanceof Heading && ((Heading) node).getLevel() == level.getLevel()) {
            // 段落标题
            String title = TEXT_CONTENT_RENDERER.render(node);
            if (StrUtil.isBlank(title)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PARAGRAPH_TITLE_IS_NOT_BLANK);
            }
            // 段落内容
            String content = getContent(node, level);
            if (StrUtil.isBlank(content)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PARAGRAPH_CONTENT_IS_NOT_BLANK);
            }
            secondLevelHeadings.add(Paragraph.of(title, content));
        }

        Node child = node.getFirstChild();
        while (child != null) {
            secondLevelHeadings.addAll(getHeadingsByLevel(child, level));
            child = child.getNext();
        }

        return secondLevelHeadings;
    }

    private static String getContent(Node node, Level level) {
        StringBuilder content = new StringBuilder();
        Node child = node.getNext();
        while (child != null && !(child instanceof Heading && ((Heading) child).getLevel() <= level.getLevel())) {
            content.append(TEXT_CONTENT_RENDERER.render(child));
            child = child.getNext();
        }

        return content.toString().trim();
    }

    public static void main(String[] args) {
        String markdown = "# First level\n" +
                "## Second level A\n一样一样 \n" +
                "### Third level\n" +
                "## Second level B\n" +
                "Some text";

        String a = "ddddddddddd,\n\ndfsfsf\nfggfgdfg\n\ngdgdgdgdfgdgdgd";

        Node document = MARKDOWN_PARSER.parse(a);
        System.out.println(TEXT_CONTENT_RENDERER.render(document));
        List<Paragraph> secondLevelHeadings = getHeadingsByLevel(document, Level.SECOND);

        System.out.println("Second level headings found:");
        for (Paragraph heading : secondLevelHeadings) {
            System.out.println(heading.toString());
        }
    }

    @Getter
    public enum Level {
        FIRST(1),
        SECOND(2),
        THIRD(3),
        FOURTH(4),
        FIFTH(5),
        SIXTH(6);

        private final int level;

        Level(int level) {
            this.level = level;
        }
    }

}
