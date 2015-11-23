package me.ziyuo.wang.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<String> chanels = null;

		/**
		 * 1.获取所有渠道列表 <br/>
		 * 2.为每个渠道列表生成工作目录 <br/>
		 * 3.工作目录中进行结构化<br/>
		 * 4.生成打包好渠道的发行包<br/>
		 * 5.清理工作空间
		 */
		try {
			/**
			 * 获取渠道列表
			 */
			chanels = readLines(CommValues.CHANELS_PATH);
			if (chanels != null && chanels.size() > 0) {
				for (String chanel : chanels) {
					// 建立渠道工作空间
					File chanelWorkDir = new File(CommValues.WORK_PATH, chanel);
					if (!chanelWorkDir.exists()) {
						chanelWorkDir.mkdir();
					}
					/**
					 * 工作目录结构化
					 */
					File apkFile = new File(chanelWorkDir, new ReleaseNameGenerator() {
						
						@Override
						public String getReleaseName(String chanelName, String apkName) {
							return chanelName + "_"
									+ CommValues.APK_LOCATION.getName();//策略设计模式，自定义ReleaseNameGenerator来生成自定义新的发行安装包名
						}
					}.getReleaseName(chanel,CommValues.APK_LOCATION.getName()));
					FileUtils.copyFile(CommValues.APK_LOCATION, apkFile);// 拷贝源发行包
					File keyDir = new File(chanelWorkDir, CommValues.META_INFO);
					if (!keyDir.exists())
						keyDir.mkdir();
					File chanelInfoFile = new File(keyDir, CommValues.PREFIX 
							+ chanel);
					System.out.println(chanelInfoFile.getAbsolutePath());
					chanelInfoFile.createNewFile();

					String cmd = "aapt a " + apkFile.getName() + " "
							+ CommValues.META_INFO + File.separator
							+ CommValues.PREFIX + chanel;
					System.out.println(cmd);
					Process process = execRuntime(cmd, chanelWorkDir);// 执行aapt
					process.waitFor();// 阻塞当前进程，等待Command命令执行完成
					if (process.exitValue() == 0) {
						System.out.println("正常执行完  Command 命令。");
					} else {
						new RuntimeException("执行 Command 命令出现异常！");
					}
					// 执行完成后就可以 输出到 out目录下了
					FileUtils.copyFileToDirectory(apkFile, CommValues.OUT_PATH);
					FileUtils.deleteDirectory(chanelWorkDir);// 单个渠道打包任务完成,删除工作空间
					System.out.println("删除工作目录:["
							+ chanelWorkDir.getAbsolutePath() + "]完成");
				}

			} else {
				System.out.println("暂无可以生成渠道包的渠道请检查渠道文件(chanels.txt)文件");
			}

		} catch (Exception e) {// 捕获所有Exception
			e.printStackTrace();
		}
	}

	/**
	 * 获取渠道号的列表(是生成渠道包的数据源)
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static List<String> readLines(String path) throws Exception {
		List<String> lines = new ArrayList<>();
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
			System.out.println("line :" + line);
		}
		bufferedReader.close();
		return lines;
	}

	public static Process execRuntime(String cmd, File runInDir)
			throws IOException {
		return Runtime.getRuntime().exec(cmd, null, runInDir);
	}
}
