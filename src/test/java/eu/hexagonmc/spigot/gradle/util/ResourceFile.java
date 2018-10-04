/**
 *
 * Copyright (C) 2017 - 2018  HexagonMc <https://github.com/HexagonMC>
Copyright (C) 2017 - 2018  Zartec <zartec@mccluster.eu>
 *
 *     This file is part of Spigot-Gradle.
 *
 *     Spigot-Gradle is free software:
 *     you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Spigot-Gradle is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Spigot-Gradle.
 *     If not, see <http://www.gnu.org/licenses/>.
 */
package eu.hexagonmc.spigot.gradle.util;

import com.google.common.base.Charsets;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceFile extends ExternalResource {

    private final String _res;
    private InputStream _stream;

    public ResourceFile(String res) {
        this._res = res;
    }

    private InputStream createInputStream() {
        return getClass().getResourceAsStream(_res);
    }

    public String getContent() throws IOException {
        InputStreamReader reader = new InputStreamReader(createInputStream(), Charsets.UTF_8);
        char[] tmp = new char[4096];
        StringBuilder b = new StringBuilder();
        try {
            while (true) {
                int len = reader.read(tmp);
                if (len < 0) {
                    break;
                }
                b.append(tmp, 0, len);
            }
            reader.close();
        } finally {
            reader.close();
        }
        return b.toString();
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        _stream = getClass().getResourceAsStream(_res);
    }

    @Override
    protected void after() {
        try {
            if (_stream != null) {
                _stream.close();
            }
        } catch (IOException e) {
            // ignore
        }
        super.after();
    }
}
