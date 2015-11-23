package me.ziyuo.wang.tools;

/**
 * 发布名称的生成策略
 * @author ziyuo Wang
 *
 */
public abstract class ReleaseNameGenerator {
	public abstract String getReleaseName(String chanelName,String apkName);
}
