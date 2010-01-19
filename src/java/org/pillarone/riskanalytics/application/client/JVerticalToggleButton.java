package org.pillarone.riskanalytics.application.client;


import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * A toggle button that paints its content rotated either right or left by
 * 90 degrees.  Note: the vertical and horizontal text position and alignment
 * properties relate to the pre-rotated orientation.
 *
 * @see javax.swing.JToggleButton
 */
public class JVerticalToggleButton extends JToggleButton {
    public static final String ROTATION_CHANGED_PROPERTY = "rotation";

    private JToggleButton fButtonDelegate;
    private JLabel fLabelDelegate;
    private CellRendererPane fRenderer;
    private Dimension fPreferredSize, fMinimumSize, fMaximumSize;
    private int fRotation;

    /**
     * Creates a vertical toggle button with no set text or icon.
     */
    public JVerticalToggleButton() {
        super();
        configure();
    }

    /**
     * Get the preferred size of this component.
     *
     * @return the value of the <code>preferredSize</code> property
     */
    public Dimension getPreferredSize() {
        fPreferredSize = flipOrientation(super.getPreferredSize());
        return fPreferredSize;
    }

    private Dimension flipOrientation(Dimension dimension) {
        return new Dimension(new Double(dimension.getHeight()).intValue(), new Double(dimension.getWidth()).intValue());
    }

    /**
     * Get the minimum size of this component.
     *
     * @return the value of the <code>minimumSize</code> property
     */
    public Dimension getMinimumSize() {
        fMinimumSize = flipOrientation(super.getMinimumSize());
        return fMinimumSize;
    }

    /**
     * Get the maximum size of this component.
     *
     * @return the value of the <code>maximumSize</code> property
     */
    public Dimension getMaximumSize() {
        fMaximumSize = flipOrientation(super.getMaximumSize());
        return fMaximumSize;
    }

    /**
     * Paint the component.
     *
     * @param g The graphics context for painting
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        fRenderer.paintComponent(g2, fButtonDelegate, this, 0, 0, getWidth(), getHeight(), true);

        ButtonModel model = getModel();
        boolean doOffset = model.isPressed() && model.isArmed();
        int offsetAmount = UIManager.getInt("Button.textShiftOffset");
        double degrees = Math.toRadians(-90);

        if (fRotation == LEFT) {
            int h = getHeight();
            g2.translate(0, h);
            g2.rotate(degrees);
            if (doOffset) {
                g2.translate(-offsetAmount, offsetAmount);
            }

            fRenderer.paintComponent(g2, fLabelDelegate, this, 0, 0, getHeight(), getWidth(), true);
            if (doOffset) {
                g2.translate(offsetAmount, -offsetAmount);
            }
            g2.rotate(-degrees);

            g2.translate(0, -h);
        } else {
            int w = getWidth();
            g2.translate(w, 0);
            g2.rotate(-degrees);
            if (doOffset) {
                g2.translate(offsetAmount, -offsetAmount);
            }
            fRenderer.paintComponent(g2, fLabelDelegate, this, 0, 0, getHeight(), getWidth(), true);
            if (doOffset) {
                g2.translate(-offsetAmount, offsetAmount);
            }
            g2.rotate(degrees);
            g2.translate(-w, 0);
        }
    }

    /**
     * Get the rotation (SwingConstants.LEFT for a -90 degree rotation or
     * SwingConstants.RIGHT for a 90 degree rotation)
     *
     * @return The rotation
     */
    public int getRotation() {
        return fRotation;
    }

    /**
     * Set the rotation (SwingConstants.LEFT for a -90 degree rotation or
     * SwingConstants.RIGHT for a 90 degree rotation)
     *
     * @param rotation The rotation
     */
    public void setRotation(int rotation) {
        if (fRotation == rotation) {
            return;
        }
        int oldValue = fRotation;
        fRotation = checkRotationKey(rotation, "rotation");
        firePropertyChange(ROTATION_CHANGED_PROPERTY, oldValue, rotation);
        repaint();
    }

    /*
     * Verify that key is a legal value for the
     * <code>rotationAlignment</code> property.
     *
     * @param key the property value to check, one of the following values:
     * <ul>
     * <li> SwingConstants.LEFT (the default)
     * <li> SwingConstants.RIGHT
     * </ul>
     *
     * @param exception the <code>IllegalArgumentException</code>
     *		detail message
     * @exception IllegalArgumentException if key is not one of the legal
     *		values listed above
     * @see #setRotation
     */
    protected int checkRotationKey(int key, String exception) {
        if (key == LEFT || key == RIGHT) {
            return key;
        } else {
            throw new IllegalArgumentException(exception);
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private void configure() {
        fRotation = LEFT;

        fButtonDelegate = new JToggleButton() {
            public String getText() {
                return null;
            }

            public boolean isEnabled() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.isEnabled();
                }
                return super.isEnabled();
            }

            public boolean isSelected() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.isSelected();
                }
                return super.isSelected();
            }

            public boolean isFocusPainted() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.isFocusPainted();
                }
                return super.isFocusPainted();
            }

            public boolean isContentAreaFilled() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.isContentAreaFilled();
                }
                return super.isContentAreaFilled();
            }

            public boolean isBorderPainted() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.isBorderPainted();
                }
                return super.isBorderPainted();
            }

            public Border getBorder() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getBorder();
                }
                return super.getBorder();
            }

            public boolean isOpaque() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.isOpaque();
                }
                return super.isOpaque();
            }

            public Color getBackground() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getBackground();
                }
                return super.getBackground();
            }

            public Icon getIcon() {
                return null;
            }

            public Icon getPressedIcon() {
                return null;
            }

            public Icon getSelectedIcon() {
                return null;
            }

            public Icon getRolloverIcon() {
                return null;
            }

            public Icon getRolloverSelectedIcon() {
                return null;
            }

            public Icon getDisabledIcon() {
                return null;
            }
        };

        fLabelDelegate = new JLabel() {
            public String getText() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getText();
                }
                return super.getText();
            }

            public Color getForeground() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getForeground();
                }
                return super.getForeground();
            }

            public boolean isEnabled() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.isEnabled();
                }
                return super.isEnabled();
            }

            public int getIconTextGap() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getIconTextGap();
                }
                return super.getIconTextGap();
            }

            public int getVerticalAlignment() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getVerticalAlignment();
                }
                return super.getVerticalAlignment();
            }

            public int getVerticalTextPosition() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getVerticalTextPosition();
                }
                return super.getVerticalTextPosition();
            }

            public int getHorizontalAlignment() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getHorizontalAlignment();
                }
                return super.getHorizontalAlignment();
            }

            public int getHorizontalTextPosition() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getHorizontalTextPosition();
                }
                return super.getHorizontalTextPosition();
            }

            public Border getBorder() {
                return null;
            }

            public int getMnemonic() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getMnemonic();
                }
                return -1;
            }

            public int getDisplayedMnemonicIndex() {
                if (JVerticalToggleButton.this != null) {
                    return JVerticalToggleButton.this.getDisplayedMnemonicIndex();
                }
                return super.getDisplayedMnemonicIndex();
            }

            private Insets insets = new Insets(0, 0, 0, 0);

            public Insets getInsets() {
                if (JVerticalToggleButton.this != null) {
                    return super.getInsets();
                }

                Border b = JVerticalToggleButton.this.getBorder();

                Insets minsets = JVerticalToggleButton.this.getMargin();

                insets.left = minsets.left;
                insets.top = minsets.top;
                insets.right = minsets.right;
                insets.bottom = minsets.bottom;

                if (b != null) {
                    Insets binsets = b.getBorderInsets(JVerticalToggleButton.this);
                    insets.left += binsets.left;
                    insets.top += binsets.top;
                    insets.right += binsets.right;
                    insets.bottom += binsets.bottom;
                }

                return insets;
            }

            public Icon getIcon() {
                if (JVerticalToggleButton.this != null) {
                    return super.getIcon();
                }

                ButtonModel model = JVerticalToggleButton.this.getModel();
                Icon subIcon = null;

                if (!model.isEnabled()) {
                    if (model.isSelected()) {
                        subIcon = getDisabledSelectedIcon();
                    } else {
                        subIcon = getDisabledIcon();
                    }
                } else if (model.isPressed() && model.isArmed()) {
                    subIcon = getPressedIcon();
                } else if (isRolloverEnabled() && model.isRollover()) {
                    if (model.isSelected()) {
                        subIcon = getRolloverSelectedIcon();
                    } else {
                        subIcon = getRolloverIcon();
                    }
                } else if (model.isSelected()) {
                    subIcon = getSelectedIcon();
                }

                if (subIcon == null)
                    subIcon = JVerticalToggleButton.this.getIcon();

                return subIcon;
            }

            public Icon getDisabledIcon() {
                return JVerticalToggleButton.this.getDisabledIcon();
            }
        };

        fLabelDelegate.setOpaque(false);

        // we paint our own border
        fButtonDelegate.setBorderPainted(false);
        fButtonDelegate.setModel(getModel());

        fRenderer = new CellRendererPane();
        fRenderer.add(fButtonDelegate);
        fRenderer.add(fLabelDelegate);


        fPreferredSize = new Dimension();
        fMinimumSize = new Dimension();
        fMaximumSize = new Dimension();

        Action action = getAction();
        if (action != null) {
            fButtonDelegate.setAction(action);
        }
    }
}
