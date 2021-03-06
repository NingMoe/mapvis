package mapvis;


import mapvis.common.datatype.Node;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;

public class RectangleCanvas extends JPanel {
        Node root;

        public RectangleCanvas(Node root) {
            this.root = root;
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            recPaintNode((Graphics2D) g,root);
        }

        void recPaintNode(Graphics2D g, Node node) {
            double x = (double) node.getVal("x");
            double y = (double) node.getVal("y");
            double r = Math.sqrt((double)node.getVal("size"))/2;

            //System.out.printf("%f,%f,%i");

            Color color = new Color((int)node.getVal("color"));
            g.setColor(color);
            //g.translate(,y);

            //new Ellipse2D.Double(x-0.1,y-0.1,2*0.1,2*0.1);
            //g.draw(new Ellipse2D.Double(x-r,y-r,2*r,2*r));
            //g.draw(new Rectangle2D.Double(x-r,y-r,2*r,2*r));

            //g.draw(new Rectangle2D.Double(x-r+100,y-r,2*r,2*r));


            double x0 = (double) node.getVal("x0");
            double x1 = (double) node.getVal("x1");
            double y0 = (double) node.getVal("y0");
            double y1 = (double) node.getVal("y1");
            g.draw(new Rectangle2D.Double(x0,y0,x1-x0,y1-y0));


            for (Node child : node.getChildren()) {
                recPaintNode(g, child);
            }
        }



        public static void start(Node root) {
            JFrame frame = new JFrame("Oval Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new RectangleCanvas(root));
            frame.setSize(1000, 1000);
            frame.setVisible(true);
        }

        public static void export(Node root) throws IOException {
            // Get a DOMImplementation
            DOMImplementation domImpl =
                    GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document
            Document document = domImpl.createDocument(null, "svg", null);

            // Create an instance of the SVG Generator
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

            // Ask the test to render into the SVG Graphics2D implementation
            RectangleCanvas canvas = new RectangleCanvas(root);
            canvas.recPaintNode(svgGenerator, root);

            // Finally, stream out SVG to the standard output using UTF-8
            // character to byte encoding
            boolean useCSS = true; // we want to use CSS style attribute


            File file = File.createTempFile("vis", ".svg");
            FileOutputStream os = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(os, "UTF-8");
            svgGenerator.stream(out, useCSS);

            Desktop.getDesktop().browse(file.toURI());
        }
    }

