/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml), Erich Pleny (erichpleny)
 * Copyright (c) 2012, FrostWire(R). All rights reserved.
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

package com.frostwire.gui.player;

import java.io.File;

import com.limegroup.gnutella.util.FrostWireUtils;

public class MediaPlayerOSX extends MediaPlayer {

    @Override
    protected String getPlayerPath() {
        boolean isRelease = !FrostWireUtils.getFrostWireJarPath().contains("frostwire-desktop");
        return (isRelease) ? getReleasePlayerPath() : getNonReleasePlayerPath();
    }

    @Override
    protected float getVolumeGainFactor() {
        return 30.0f;
    }
    
    private String getReleasePlayerPath() {
        String javaHome = System.getProperty("java.home");
        File f = new File(javaHome).getAbsoluteFile();
        f = f.getParentFile(); // Contents
        f = f.getParentFile(); // jre
        f = f.getParentFile(); // PlugIns
        f = f.getParentFile(); // Contents
        f = new File(f, "MacOS" + File.separator + "fwplayer_osx");

        return f.getAbsolutePath();
    }

    private String getNonReleasePlayerPath() {
        String frostWireJarPath = FrostWireUtils.getFrostWireJarPath();
        String pathPreffix = frostWireJarPath.substring(0, frostWireJarPath.indexOf("frostwire-desktop"));
        System.out.println("Non Release Path: " + pathPreffix + "frostwire-desktop/lib/native/fwplayer_osx");
        return pathPreffix + "frostwire-desktop/lib/native/fwplayer_osx";
    }
}