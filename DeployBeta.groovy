/**
 * Copyright 2014 Expedia, Inc. All rights reserved.
 * EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

import com.expedia.platform.tools.branchbot.LogScanner
import com.expedia.platform.tools.branchbot.ServiceController
import com.expedia.platform.tools.branchbot.Target

// Edit this table to specify deployment target settings
def targets =
[
	//          Host name      Service name			Do unzip	awaitBackfill
//	new Target("PHELMCBETR001", "multicache-router", false,		false),
//	new Target("PHELMCBETR002", "multicache-router", false,		false),
	new Target("PHELMCBETA001", "multicache-server", false,		false),
//	new Target("PHELMCBETA002", "multicache-server", false,		true),
//	new Target("PHELMCBETB001", "multicache-server", false,		true),
//	new Target("PHELMCBETB002", "multicache-server", false,		true),
//	new Target("CHELSTRPL002", "multicache-router", false,		false),
//	new Target("CHELSTRPL003", "multicache-router", true,		false),
//	new Target("CHELSTRPL004", "multicache-server", true,		false),
//	new Target("CHELSTRPL005", "multicache-server", true,		false)
]

// Edit these settings to control version and release type to be deployed
//def type        = "release"
//def type        = "releasecandidate"
def type        = "continuousintegration"
def version     = "pMain.0.0.844212"
//def version   = "2.1.5"
//def version   = "2.3.6"

// These settings shouldn't need to be edited regularly for deployment
def product     = "com.expedia.e3.platform.multicache.product.serverline"
def depreps     = "\\\\karmalab.net\\builds\\depreps"
String zipFile  = "${depreps}\\${type}\\${product}\\${product}-${version}.zip"
//def outDir    = "d\$\\MultiCache\\${version}"
def outDir      = "d\$\\MultiCache\\${version}"
def logDir      = "d\$\\MultiCache\\Logs\\Event.log"

def confPath = "d\$\\tanuki\\win\\conf\\wrapper.conf"
def replaceTarget = "set.wrapper.multicache.root=D:\\\\MultiCache\\\\[A-Za-z0-9.-]*\\\\Server"
def replaceWith = "set.wrapper.multicache.root=D:\\\\MultiCache\\\\${version}\\\\Server"

private void unzipProduct(String zipFile, String zipOutputDir)
{
	def ant = new AntBuilder();

	// Start with clean directory or ant unzip will take a long time and complain
	// that it can't change date / time on target files.
	ant.delete(dir: zipOutputDir)

	ant.unzip(src: zipFile, dest: zipOutputDir, overwrite: "true")
	{
		mapper(type: "identity")
	}
}

def String replaceStringInFile(String fileName, String target , String replacement, boolean beVerbose)
{
	fileName = normalizePath(fileName)

	if (beVerbose)
	{
		println "Replacing fileName = [${fileName}] target = [${target}], replacement = [${replacement}]"
	}
	def file = new File(fileName).asWritable()
	def fileText = file.text

	def newText = fileText.replaceAll("${target}", replacement)
	file.write(newText)

	return newText
}

def String normalizePath(String toBeNormalized)
{
	File file = new File (toBeNormalized)
	return file.getAbsolutePath()
}

final String BACKFILL_SUCCESS = "54154"
final String BACKFILL_FAILURE = "54155"
final String BACKFILL_NOSERVER = "54148"

deployTargets =
{
	Target target ->

	String confFileName = "\\\\${target.hostName}\\${confPath}"

	ServiceController.stop(target.hostName, target.serviceName, true)

	if (target.doUnzip)
	{
		println "Unzipping to ${target.hostName}..."

		// temporary hack to deploy private build
//		String zipFileHack = "D:\\build\\multicache\\deliverables\\com.expedia.e3.platform.multicache.product" + ".serverline\\com.expedia.e3.platform.multicache.product.serverline-2.2.1.private.zip"
//		unzipProduct(zipFileHack, "\\\\${hostName}\\${outDir}")
		unzipProduct(zipFile, "\\\\${target.hostName}\\${outDir}")
	}

	println "Updating Tanuki configuration file ${confFileName} with ${replaceWith}"
	replaceStringInFile(confFileName, replaceTarget, replaceWith, false)

	// Don't want to poll for service status if we're going to be tailing log looking for backfill results
	ServiceController.start(
		target.hostName,
		target.serviceName,
		!target.awaitBackfill)	// If true, ServiceController will poll until service has started

	if (target.awaitBackfill)
	{
		String logFileName = "\\\\${target.hostName}\\${logDir}"
//		String logFileName = "d:\\MultiCache\\Log\\app\\event.log"

		// Ensure log file exists (needed for host on which service has not been run before)
		File file = new File (logFileName)
		while (!file.exists())
		{
			Thread.sleep(50)
		}

		LogScanner logScanner = new LogScanner(logFileName)

		logScanner.addSearchPattern(BACKFILL_SUCCESS)
		logScanner.addSearchPattern(BACKFILL_FAILURE)
		logScanner.addSearchPattern(BACKFILL_NOSERVER)

		println "Scanning tail of log file ${logFileName}"

		String result = logScanner.scan(
			1000,		// read interval MS
			1200);		// timeout seconds

		if (null == result)
		{
			println "Timeout exceeded while attempting to detect backfill"
			println "Halting deployment"
			System.exit(1)
		}

		if (result.contains(BACKFILL_FAILURE) || result.contains(BACKFILL_NOSERVER))
		{
			println "Backfill FAILURE detected!"
			println result
			println ""
			println "Halting deployment"
			System.exit(54155)
		}

		if (result.contains(BACKFILL_SUCCESS))
		{
			println "Backfill successfully completed"
			println result
		}
	}
}

/***********************************************************************************************************************
 * Entry point of script
 *
 */
targets.each(deployTargets)
