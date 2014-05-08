package mapvis.liquidvis.gui.actions;

import mapvis.common.algorithm.FTAOverlapRemoval;
import mapvis.liquidvis.gui.RenderAction;
import mapvis.liquidvis.model.*;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.stream.Collectors;

public class LabelRender<T> implements RenderAction {
    private MapModel<T> model;

    class Entry{
        public Entry (T element){
            this.element = element;
        }

        public T element;
        public String text;
        public Rectangle2D bounds;
        private Point2D anchor;
        public int level;

        public Rectangle2D textRect;
        public Point2D textOrgin;
    }

    Map<T, Entry> entries = new HashMap<>();

    protected Rectangle2D getBounds(T node) {
        return ((Area) model.getValue(node, "__area")).getBounds2D();
    }
    protected int getLevel(T node) {
        return ((int) model.getValue(node, "__level"));
    }
    protected String getText(T node) {
        return ((String) model.getValue(node, "__label.text"));
    }
    protected Point2D getAnchor(T node) {
        mapvis.liquidvis.model.Polygon polygon = model.getPolygon(node);

        if (polygon != null){
            return polygon.calcCentroid();
        }
        else {
            Rectangle2D area = ((Area) model.getValue(node, "__area")).getBounds2D();
            return new Point2D.Double(area.getCenterX(), area.getCenterY());
        }

    }

    public LabelRender(MapModel<T> model){
        this.model = model;
        entries = model.getLeaves().stream()
                .map(n -> new Entry(n))
                .collect(Collectors.toMap(e -> e.element, e -> e));
    }

    public void update() {
        FontRenderContext ctx = new FontRenderContext(null, false, false);

        entries = model.getAllNodes().stream()
                .filter(n -> getLevel(n) > 0)
                .filter(n -> getText(n) != null)
                .map(n -> new Entry(n))
                .collect(Collectors.toMap(e -> e.element, e -> e));

        for (Entry entry : entries.values()) {
            entry.text = getText(entry.element);
            entry.level = getLevel(entry.element);
            entry.bounds = getBounds(entry.element);
            entry.anchor = getAnchor(entry.element);

            Font font = getFont(entry.level);

            Rectangle2D strBounds = font.getStringBounds(entry.text, ctx);
            entry.textRect = new Rectangle2D.Double(
                    -strBounds.getWidth()/2  + entry.anchor.getX(),
                    -strBounds.getHeight()/2 + entry.anchor.getY(),
                    strBounds.getWidth(),strBounds.getHeight());
            entry.textOrgin = new Point2D.Double(strBounds.getX(), strBounds.getY());
        }
        layout();
    }

    public void layout(){
        java.util.List<Entry> collect = entries.values().stream().filter(e -> e.level > 2).collect(Collectors.toList());
        FTAOverlapRemoval<Entry> removal = new FTAOverlapRemoval<>( collect, e -> e.textRect);
        removal.run();
        for (Entry entry : collect) {
            entry.textRect =  removal.getRectangle(entry);
        }

        collect = entries.values().stream().filter(e -> e.level == 2).collect(Collectors.toList());
        removal = new FTAOverlapRemoval<>( collect, e -> e.textRect);
        removal.run();
        for (Entry entry : collect) {
            entry.textRect =  removal.getRectangle(entry);
        }

        collect = entries.values().stream().filter(e -> e.level == 1).collect(Collectors.toList());
        removal = new FTAOverlapRemoval<>( collect, e -> e.textRect);
        removal.run();
        for (Entry entry : collect) {
            entry.textRect =  removal.getRectangle(entry);
        }
    }

    public void draw(Graphics2D g){
        for (Entry entry : entries.values()) {
            if (entry.level == 3)
                renderLabel(g, entry);
        }
        for (Entry entry : entries.values()) {
            if (entry.level == 2)
                renderLabel(g, entry);
        }
        for (Entry entry : entries.values()) {
            if (entry.level == 1)
                renderLabel(g, entry);
        }
    }

    public void renderLabel(Graphics2D g, Entry entry){
        if (entry.level == 0){
            return;
        }
        if (entry.level <= 3){
            g.setFont(getFont(entry.level));
            g.setColor(getColor(entry.level));

            g.drawString(entry.text,
                    (float)(entry.textRect.getX() - entry.textOrgin.getX()),
                    (float)(entry.textRect.getY() - entry.textOrgin.getY()));
        }
    }

    private Font[] fonts;
    {
        Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

        fonts = new Font[] {
            new Font("Arial", Font.BOLD, 64).deriveFont(fontAttributes),
            new Font("Arial", Font.BOLD, 32),
            new Font("Arial", Font.BOLD, 16)
        };
    }

    protected Font getFont(int level){
        level = Math.min(3, level);
        level = Math.max(1, level);
        return fonts[level-1];
    }

    private Color[] colors = {Color.BLACK, Color.BLACK, Color.decode("#004000")};
    protected Color getColor(int level){
        level = Math.min(3, level);
        level = Math.max(1, level);
        return colors[level-1];
    }
}
