package com.gitlab.jeeto.oboco.plugin.hash.jdk;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.hash.HashManager;
import com.gitlab.jeeto.oboco.plugin.hash.HashType;

public class JdkHashPlugin extends Plugin {
	
    public JdkHashPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }
	
    @Extension
    public static class JdkHashManager implements HashManager.Sha256HashManager {

		@Override
		public String createHash(TypeableFile inputFile, HashType outputHashType) throws Exception {
			MessageDigest md = null;
			
			if(HashType.SHA256.equals(outputHashType)) {
				md = MessageDigest.getInstance("SHA-256");
			}
			
			if(md == null) {
				throw new Exception("extension not found.");
			}
			
	        InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(inputFile);
				
				byte[] buffer = new byte[8 * 1024];
			    int bufferSize;
			    while ((bufferSize = inputStream.read(buffer)) != -1) {
			    	md.update(buffer, 0, bufferSize);
			    }
			} finally {
				if(inputStream != null) {
					try {
						inputStream.close();
					} catch(Exception e) {
						// pass
					}
				}
			}
			
			byte[] hash = md.digest();
			
	        StringBuilder sb = new StringBuilder();
	        for(int i = 0; i < hash.length; i = i + 1) {
	            sb.append(String.format("%02x", hash[i]));
	        }
	        return sb.toString();
		}
		
    }
}