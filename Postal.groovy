/**
 * Copyright 2013 Expedia, Inc. All rights reserved.
 * EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

File file = new File ("d:/dev/postal.CAN/combined.tsv")

List<String[]> table = new ArrayList<String[]>()

def eachLine =
{
	String line = (String) it
	String[] tokens = line.split("\t")

	table.add(tokens)
}

file.each(eachLine)

File outFile = new File ("d:/dev/postal.CAN/postalCodes.CAN.manual.txt")

if (outFile.exists())
{
	assert outFile.delete()
	assert outFile.createNewFile()
}

boolean append = true
FileWriter fileWriter = new FileWriter(outFile, append)
BufferedWriter buffWriter = new BufferedWriter(fileWriter)

def width = table[0].length

try
{
	for (int column = 0; column < width; column++)
	{
		table.each
		{
			String[] tokens = (String[]) it
			String name = tokens[column]
			String id = tokens[0]

			StringBuilder stringBuilder = new StringBuilder()

			stringBuilder.append(name)
			stringBuilder.append("\t")
			stringBuilder.append(id)

			buffWriter.writeLine(stringBuilder.toString())
		}
	}
}
finally
{
	buffWriter.close()
	System.exit(0)
}
