/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2014, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.gui.library;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.frostwire.gui.theme.ThemeMediator;
import com.limegroup.gnutella.gui.I18n;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class NodeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (sel) {
            setBackgroundSelectionColor(ThemeMediator.TABLE_SELECTED_BACKGROUND_ROW_COLOR);
        } else {
            setBackgroundNonSelectionColor(row % 2 == 0 ? Color.WHITE : ThemeMediator.TABLE_ALTERNATE_ROW_COLOR);
        }

        if (value instanceof DirectoryHolderNode) {
            DirectoryHolderNode node = (DirectoryHolderNode) value;
            DirectoryHolder dh = node.getDirectoryHolder();
            setText(dh.getName());
            setToolTipText(dh.getDescription());
            Icon icon = dh.getIcon();
            if (icon != null) {
                setIcon(icon);
            }
        } else if (value instanceof DevicesNode) {
            DevicesNode node = (DevicesNode) value;
            setIcon(leaf ? node.getMinusDevices() : node.getPlusDevices());
        } else if (value instanceof DeviceNode) {
            DeviceNode node = (DeviceNode) value;
            setIcon(leaf ? node.getMinusIcon() : node.getPlusIcon());
            if (node.getDevice().isLocal()) {
                setText(I18n.tr("My files"));
            }
        } else if (value instanceof DeviceFileTypeTreeNode) {
            DeviceFileTypeTreeNode node = (DeviceFileTypeTreeNode) value;
            setIcon(node.getIcon());
            setText(node.getUserObject().toString());
        }

        return this;
    }
}
