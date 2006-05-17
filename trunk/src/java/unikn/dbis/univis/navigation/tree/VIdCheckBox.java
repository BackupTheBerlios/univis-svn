package unikn.dbis.univis.navigation.tree;

import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.meta.VDataReference;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Set;
import java.util.HashSet;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VIdCheckBox</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 10.04.2006
 * Time: 19:17:20
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VIdCheckBox extends JCheckBoxMenuItem {

    private VDimension dimension;

    private Long id;
    private Long parentId;

    /**
     * TODO: document me!!!
     *
     * @param
     */
    public VIdCheckBox(final VDimension dimension, final Long id, String text) {
        super(text);

        this.dimension = dimension;
        this.id = id;

        //setSelected(dimension.getSelections().contains(id));

        addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (isSelected()) {
                    dimension.getSelections().add(new Parentor(id, parentId));
                }
                else {
                    dimension.getSelections().remove(new Parentor(id, parentId));

                    for (VDataReference dataReference : dimension.getChildren()) {
                        if (dataReference instanceof VDimension) {
                            test((VDimension) dataReference, id);
                        }
                    }
                }
            }
        });
    }

    /**
     * TODO: document me!!!
     *
     * @param
     */
    public VIdCheckBox(final VDimension dimension, final Long id, final Long parentId, String text) {
        super(text);

        this.dimension = dimension;
        this.id = id;
        this.parentId = parentId;

        //setSelected(dimension.getSelections().contains(id));

        addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (isSelected()) {
                    dimension.getSelections().add(new Parentor(id, parentId));
                }
                else {
                    dimension.getSelections().remove(new Parentor(id, parentId));

                    for (VDataReference dataReference : dimension.getChildren()) {
                        if (dataReference instanceof VDimension) {
                            test((VDimension) dataReference, id);
                        }
                    }
                }
            }
        });
    }

    public class Parentor {

        private Long id;
        private Long parentId;

        public Parentor(Long id, Long parentId) {
            this.id = id;
            this.parentId = parentId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Parentor parentor = (Parentor) o;

            if (id != null ? !id.equals(parentor.id) : parentor.id != null) return false;
            return !(parentId != null ? !parentId.equals(parentor.parentId) : parentor.parentId != null);
        }

        public int hashCode() {
            int result;
            result = (id != null ? id.hashCode() : 0);
            result = 29 * result + (parentId != null ? parentId.hashCode() : 0);
            return result;
        }
    }

    public void test(VDimension dimension, Long id) {

        Set<Object> selections = dimension.getSelections();

        // !!! Fixes the concurrent modification exception.
        Set<Object> removables = new HashSet<Object>();

        for (Object o : selections) {
            if (o instanceof Parentor) {
                Parentor parentor = (Parentor) o;

                if (id.equals(parentor.getParentId())) {
                    removables.add(parentor);

                    for (VDataReference dataReference : dimension.getChildren()) {
                        if (dataReference instanceof VDimension) {
                            test((VDimension) dataReference, parentor.getId());
                        }
                    }
                }
            }
        }

        selections.removeAll(removables);
    }

    @Override
    protected void paintComponent(Graphics g) {

        for (Object o : dimension.getSelections()) {
            if (o instanceof Parentor) {
                Parentor parentor = (Parentor) o;

                if (parentor.getId().equals(id)) {
                    setSelected(true);
                    break;
                }
            }
        }

        super.paintComponent(g);
    }
}