package com.github.diamond.client;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.CollectionUtils;

import com.github.diamond.client.util.PropertiesFileUtils;
/**
 * @author junzhang12@iflytek.com
 * @Date 2018/6/12 20:34
 *
 */
public class DiamondPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(DiamondPropertyPlaceholderConfigurer.class);

	private static Properties properties = new Properties();

	private static final String DEFAULT_FILE_ENCODING = "utf-8";
	private String fileEncoding = DEFAULT_FILE_ENCODING;
	private boolean useDiamond = true;

	@Override
	protected Properties mergeProperties() throws IOException {
		Properties result = new Properties();
		// 是否加载本地
		if (this.localOverride) {
			// Load properties from file upfront, to let local properties
			// override.
			loadProperties(result);
		}

		if (this.localProperties != null) {
			for (Properties localProp : this.localProperties) {
				CollectionUtils.mergePropertiesIntoMap(localProp, result);
			}
		}

		// 是否已经加载过配置，防止2次加载
		if(PropertiesFileUtils.getInstance().propertiesLoaded()){
			result.putAll(properties);
		}else{
			if (this.useDiamond) {
				loadDiamondConfig(result);
			}	
		}
	

		if (!this.localOverride) {
			// Load properties from file afterwards, to let those properties
			// override.
			loadProperties(result);
		}

		return result;
	}

	private void loadDiamondConfig(Properties result) throws IOException {
		// 加载diamond配置
		Properties diamondProperties = new Properties();
	    loadProperties(diamondProperties);
		PropertiesFileUtils.getInstance().fillProperties(diamondProperties);
		PropertiesConfigurationFactoryBean propertiesConfiguration = new PropertiesConfigurationFactoryBean();
		try {
			CollectionUtils.mergePropertiesIntoMap(propertiesConfiguration.getObject(), result);
		} catch (Exception e) {
			logger.warn("没有获取到配置", e);
		}
	}

	@Override
	protected void convertProperties(Properties props) {
		super.convertProperties(props);
		properties.putAll(props);
	}

	public static Properties getProperties() {
		return properties;
	}

	public static String getString(String key) {
		return properties.getProperty(key);
	}

	public static String getString(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public boolean isUseDiamond() {
		return useDiamond;
	}

	public void setUseDiamond(boolean useDiamond) {
		this.useDiamond = useDiamond;
	}
}
