/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.data;

import java.util.List;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.Metadata;

/**
 *
 * @author loge
 */
public interface MetadataDAO {
    List<Metadata> getMetadata(AnnotationType type);
}
