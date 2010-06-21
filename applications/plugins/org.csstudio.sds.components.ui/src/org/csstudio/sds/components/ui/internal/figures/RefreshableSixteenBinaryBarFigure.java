package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Sixteen Bit Binary Bar widget figure.
 *
 * @author Alen Vrecko, Joerg Rathlev
 */
public class RefreshableSixteenBinaryBarFigure extends RectangleFigure implements IAdaptable {

    /**
     * The orientation (horizontal==true | vertical==false).
     */
    private boolean _orientationHorizontal = true;

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    private int _value;

    private Color _onColor;

    private Color _offColor;

    private int _internalFrameThickness;

    private Color _internalFrameColor;

    private OnOffBox[] _boxes;

    private GridLayout _layout;

    private int _bitRangeFrom = 0;

    private int _bitRangeTo = 15;

    private boolean _showLabels;

    private Font _labelFont;

    private Color _labelColor;

    private ICrossedFigure _crossedOutAdapter;

    private IRhombusEquippedWidget _rhombusAdapter;

    /**
     *
     * Constructor.
     */
    public RefreshableSixteenBinaryBarFigure() {
        createLayoutAndBoxes();
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        Rectangle bound = getBounds().getCopy();
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);


    }

    /**
     * Creates the layout and the boxes for this figure.
     */
    private void createLayoutAndBoxes() {
        int column = 1;
        if (_orientationHorizontal) {
            column = numberOfBits();
        }
        _layout = new GridLayout(column, false);
        _layout.horizontalSpacing = 0;
        _layout.verticalSpacing = 0;
        _layout.marginHeight = 0;
        _layout.marginWidth = 0;

        setLayoutManager(_layout);

        _boxes = new OnOffBox[numberOfBits()];
        for (int i = 0; i < numberOfBits(); i++) {
            OnOffBox box = new OnOffBox(_bitRangeFrom + i);
            box.setShowLabel(_showLabels);
            box.setLabelFont(_labelFont);
            box.setLabelColor(_labelColor);
            applyInternalBorderSettings(box);
            add(box);

            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.FILL;
            gridData.verticalAlignment = GridData.FILL;
            gridData.grabExcessVerticalSpace = true;
            gridData.grabExcessHorizontalSpace = true;

            setConstraint(box, gridData);
            _boxes[i] = box;
        }
    }

    /**
     * Removes the boxes from this figure, then recreates them and refreshes the layout.
     */
    private void recreateLayoutAndBoxes() {
        removeBoxes();
        createLayoutAndBoxes();
        updateBoxes();
    }

    /**
     * Removes the boxes from this figure.
     */
    private void removeBoxes() {
        removeAll();
    }

    /**
     * Updates the on/off state of the boxes based on the current value.
     */
    private void updateBoxes() {
        int multi = 1;
        if (_bitRangeFrom > _bitRangeTo) {
            multi = -1;
        }
        for (int i = 0; i < numberOfBits(); i++) {
            _boxes[i].setOn( ( (1 << (_bitRangeFrom + (multi * i))) & _value) != 0);
        }
    }

    /**
     * Sets the orientation (horizontal==true | vertical==false).
     *
     * @param horizontal The orientation.
     */
    public void setHorizontal(final boolean horizontal) {
        _orientationHorizontal = horizontal;

        if (_orientationHorizontal) {
            _layout.numColumns = numberOfBits();
        } else {
            _layout.numColumns = 1;
        }

        revalidate();

    }

    /**
     *
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if(adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if(adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }

        return null;
    }

    /**
     *
     * @param showLabels .
     */
    public void setShowLabels(final Boolean showLabels) {
        _showLabels = showLabels;
        for (OnOffBox box : _boxes) {
            box.setShowLabel(showLabels);
        }
    }

    /**
     *
     * @param newValue the new Binary value
     */
    public void setValue(final Integer newValue) {
        _value = newValue;
        updateBoxes();
    }

    /**
     *
     * @param font .
     */
    public void setLabelFont(final Font font) {
        _labelFont = font;
        for (OnOffBox box : _boxes) {
            box.setLabelFont(font);
        }
    }

    /**
     *
     * @param onColor .
     */
    public void setOnColor(final Color onColor) {
        if (onColor == null) {
            return;
        }
        _onColor = onColor;
    }

    /**
     *
     * @param offColor .
     */
    public void setOffColor(final Color offColor) {
        if (offColor == null) {
            return;
        }
        _offColor = offColor;
    }

    /**
     *
     * @param internalFrameThickness .
     */
    public void setInternalBorderThickness(final int internalFrameThickness) {
        _internalFrameThickness = internalFrameThickness;
        for (OnOffBox box : _boxes) {
            applyInternalBorderSettings(box);
        }
    }

    /**
     *
     * @param internalFrameColor .
     */
    public void setInternalBorderColor(final Color internalFrameColor) {
        _internalFrameColor = internalFrameColor;
        for (OnOffBox box : _boxes) {
            applyInternalBorderSettings(box);
        }
    }

    /**
     * Applies the current settings for the internal frame thickness and color to the given box.
     *
     * @param box the box.
     */
    private void applyInternalBorderSettings(final OnOffBox box) {
        if (_internalFrameColor != null) {
            box.setInternalFrame(_internalFrameThickness, _internalFrameColor);
        } else {
            box.setInternalFrame(_internalFrameThickness, null);
        }
    }

    /**
     * @param labelColor .
     */
    public final void setLabelColor(final Color labelColor) {
        if (labelColor == null) {
            return;
        }
        _labelColor = labelColor;

        for (OnOffBox box : _boxes) {
            box.setLabelColor(_labelColor);
        }
    }

    /**
     * Sets the range of bits that are displayed by this figure.
     *
     * @param from the index of the lowest bit to display.
     * @see #setBitRangeTo(int)
     */
    public void setBitRangeFrom(final int from) {
        this._bitRangeFrom = from;
        recreateLayoutAndBoxes();
    }

    /**
     * Sets the range of bits that are displayed by this figure.
     *
     * @param to the index of the highest bit to display.
     * @see #setBitRangeFrom(int)
     */
    public void setBitRangeTo(final int to) {
        this._bitRangeTo = to;
        recreateLayoutAndBoxes();
    }

    /**
     * Returns the number of bits displayed in this figure.
     *
     * @return the number of bits displayed in this figure.
     */
    private int numberOfBits() {
        if (_bitRangeTo > _bitRangeFrom) {
            return _bitRangeTo - _bitRangeFrom + 1;
        }
        return _bitRangeFrom - _bitRangeTo + 1;
    }

    /**
     * A box used to display the state of a single bit. A box is in either on or off state, and
     * displays in a different color depending on that state. It can also optionally display a label
     * with the index of the bit it displays.
     */
    private class OnOffBox extends Figure {

        private final Label _label;
        private final int _bitIndex;
        private boolean _isOn;

        public OnOffBox(final int bitIndex) {
            this._bitIndex = bitIndex;

            BorderLayout layout = new BorderLayout();
            layout.setVerticalSpacing(0);

            setBorder(new LineBorder(0));
            setLayoutManager(layout);

            _label = new Label("");

            add(_label);
            setConstraint(_label, BorderLayout.CENTER);
        }

        public void setLabelFont(final Font font) {
            _label.setFont(font);
        }

        @Override
        protected void paintFigure(final Graphics graphics) {
            super.paintFigure(graphics);
            setBackgroundColor(_isOn ? _onColor : _offColor);
            graphics.fillRectangle(getClientArea());
        }

        public void setLabelColor(final Color color) {
            _label.setBackgroundColor(color);
            _label.setForegroundColor(color);
        }

        public void setInternalFrame(final int thickness, final Color color) {
            if (color != null) {
                if (thickness > 0) {
                    if (color != null) {
                        setBorder(new LineBorder(color, thickness));
                    } else {
                        setBorder(new LineBorder(thickness));
                    }
                } else {
                    setBorder(null);
                }
                revalidate();
            }
        }

        /**
         * Sets whether this box is on.
         *
         * @param isOn whether this box is on.
         */
        public void setOn(final boolean isOn) {
            this._isOn = isOn;
        }

        /**
         * Sets whether this box displays a label.
         *
         * @param show whether this box displays a label.
         */
        public void setShowLabel(final boolean show) {
            if (show) {
                _label.setText(String.format("%02d", _bitIndex));
            } else {
                _label.setText("");
            }
        }

    }

}
