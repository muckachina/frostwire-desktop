/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2013, FrostWire(R). All rights reserved.
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

package com.frostwire.search.frostclick;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frostwire.search.PagedWebSearchPerformer;
import com.frostwire.search.SearchResult;
import com.frostwire.search.UserAgent;

/**
 * @author gubatron
 * @author aldenml
 *
 */
public class FrostClickSearchPerformer extends PagedWebSearchPerformer {
    private static final int MAX_RESULTS = 1;
    private Map<String, String> CUSTOM_HEADERS;
    private static final Logger LOG = LoggerFactory.getLogger(FrostClickSearchPerformer.class);

    public FrostClickSearchPerformer(long token, String keywords, int timeout, UserAgent userAgent) {
        super(token, keywords, timeout, MAX_RESULTS);
        initCustomHeaders(userAgent);       
    }

    private void initCustomHeaders(UserAgent userAgent) {
        if (CUSTOM_HEADERS == null) {
            CUSTOM_HEADERS = new HashMap<String, String>();
            CUSTOM_HEADERS.putAll(userAgent.getHeadersMap());
            CUSTOM_HEADERS.put("User-Agent", userAgent.toString());
            CUSTOM_HEADERS.put("sessionId", userAgent.getUUID());
        }
    }

    @Override
    protected String getUrl(int page, String encodedKeywords) {
        return "http://api.frostclick.com/q?page=" + page + "&q=" + encodedKeywords;
    }

    @Override
    protected List<? extends SearchResult> searchPage(int page) {
        String url = getUrl(page, getEncodedKeywords());
        String text = fetch(url, null, CUSTOM_HEADERS);
        if (text != null) {
            return searchPage(text);
        } else {
            LOG.warn("Page content empty for url: " + url);
            return Collections.emptyList();
        }
    }

    @Override
    protected List<? extends SearchResult> searchPage(String page) {
        // unused for this implementation since we still don't have search responses ready.
        return Collections.emptyList();
    }
}