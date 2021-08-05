package org.moon.frangura.assets;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.terasology.jnlua.LuaException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public class FranguraAsset {

    protected Path path;

    protected FranguraAsset(Path assetPath) {
        this.path = assetPath;
    }

    protected Optional<InputStream> getInputStream() {
        try {
            InputStream inputStream = new FileInputStream(path.toFile());
            return Optional.of(inputStream);
        } catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }


}
