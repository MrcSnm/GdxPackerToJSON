package atlasToJson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

/**
 * @author: Marcelo Silva Nascimento Mancini(aka Hipreme)
 * @since: 27/08/2019 (DD/MM/AAAA)
 * @version: 1.0
 */
public class AtlasToJSON 
{
  
   public static boolean checkFileExistence(String filename) 
   {
      File f = new File(filename);
      return f.exists();
   }


   public static String checkExceptions(String[] args) 
   {
      int argc = args.length;
      String filename = "";
      if (argc == 0) 
      {
         System.out.println("No input file detected, starting file dialog.");
         return "";
      } 
      else if (argc > 3) 
      {
         System.out.println("Too many arguments, ignoring procedure");
         return "";
      } 
      else 
      {
         filename = args[0];
         if (!checkFileExistence(filename)) 
         {
            System.out.println("File named " + filename + " does not exists");
            return "";
         } 
         else if (!filename.contains(".atlas")) 
         {
            System.out.println("Input file must be a .atlas file generated from LibGDX");
            return "";
         } 
      }
      return filename;
   }

   public static String getFileName(String fileDir) 
   {
      int index = 0;

      for(int i = 0, len = fileDir.length(); i < len; i++)
      {
         if(fileDir.charAt(i) == '\\' || fileDir.charAt(i) == '/')
            index = i;
      }
      return fileDir.substring(index+1);
   }

   public static String tab() 
   {
      return "\t";
   }

   public static String quote() 
   {
      return "\"";
   }

   public static String enter() 
   {
      return "\n";
   }

   public static String getPath(String file)
   {
      char ab[] = file.toCharArray();
      int len = file.length();
      int counter = 0;
      while(ab[len - 1 - counter] != '\\' && ab[len - 1 - counter] != '/')
      {
         if(counter == len - 1)
            return "";
         else
            ab[len - 1 - counter] = '\0';
         counter++;
      }
      return file.substring(0, len - counter);
   }

   public static String addFrame(int x, int y, int w, int h) {
      String frame = "";
      frame = frame + enter() + tab() + tab() + quote() + "frame" + quote() + " : {";
      frame = frame + quote() + "x" + quote() + ":" + x + ", " + quote() + "y" + quote() + ":" + y + ", ";
      frame = frame + quote() + "w" + quote() + ":" + w + ", " + quote() + "h" + quote() + ":" + h + "}";
      frame = frame + ",";
      return frame;
   }
  
   public static String addSpriteSourceSize(int w, int h) {
      String spriteSourceSize = "";
      spriteSourceSize = spriteSourceSize + enter() + tab() + tab() + quote() + "spriteSourceSize" + quote() + " : {";
      spriteSourceSize = spriteSourceSize + quote() + "x" + quote() + ":" + 0 + ", " + quote() + "y" + quote() + ":" + 0 + ", ";
      spriteSourceSize = spriteSourceSize + quote() + "w" + quote() + ":" + w + ", " + quote() + "h" + quote() + ":" + h + "}";
      spriteSourceSize = spriteSourceSize + ",";
      return spriteSourceSize;
   }

   public static String addSourceSize(int w, int h) {
      String sourceSize = "";
      sourceSize = sourceSize + enter() + tab() + tab() + quote() + "sourceSize" + quote() + " : {";
      sourceSize = sourceSize + quote() + "w" + quote() + ":" + w + ", " + quote() + "h" + quote() + ":" + h + "}";
      return sourceSize;
   }

   public static String fileRead(String filename) 
   {
      String resultingContent = "{\n\t\"frames\" : \n\t[{\n\t";

      try 
      {
            BufferedReader file = new BufferedReader(new FileReader(filename));

            while(!file.readLine().contains("repeat")) {}

            String[] buffer = new String[2];
            int currOriginX = 0;
            int currOriginY = 0;
            int currWidth = 0;
            int currHeight = 0;
            String line = file.readLine();
            resultingContent = resultingContent + tab() + quote() + "filename" + quote() + " : " + quote() + line + quote() + ",";

            while((line = file.readLine()) != null) 
            {
                if (line.contains("rotate:")) 
                {
                    resultingContent = resultingContent + enter() + tab() + tab() + quote() + "rotated" + quote() + ":";
                    line = line.replaceFirst("rotate: ", "");
                    resultingContent = resultingContent + " " + Boolean.parseBoolean(line) + ",";
                    resultingContent+= enter() + tab() + tab() + quote() + "trimmed" + quote() + ":" + " false,";
                }
                else if (line.contains("xy:")) 
                {
                    line = line.replaceFirst("xy: ", "");
                    line = line.replaceAll(" ", "");
                    buffer = line.split(",");
                    currOriginX = Integer.parseInt(buffer[0]);
                    currOriginY = Integer.parseInt(buffer[1]);
                }
                else if (line.contains("orig:")) 
                {
                    line = line.replaceFirst("orig: ", "");
                    line = line.replaceAll(" ", "");
                    buffer = line.split(",");
                    currWidth = Integer.parseInt(buffer[0]);
                    currHeight = Integer.parseInt(buffer[1]);
                }
                else if (line.contains("size:")) 
                {
                    line = line.replaceFirst("size: ", "");
                    line = line.replaceAll(" ", "");
                    buffer = line.split(",");
                    currWidth = Integer.parseInt(buffer[0]);
                    currHeight = Integer.parseInt(buffer[1]);
                }
                else if (line.contains("index:") || line.contains("offset:")) 
                {
                   
                }
                else
                {
                  if (currWidth != 0 && currHeight != 0) 
                  {
                      resultingContent = resultingContent + addFrame(currOriginX, currOriginY, currWidth, currHeight);
                      resultingContent = resultingContent + addSpriteSourceSize(currWidth, currHeight);
                      resultingContent = resultingContent + addSourceSize(currWidth, currHeight);
                      resultingContent = resultingContent + "\n";
                  }
  
                  currOriginX = 0;
                  currOriginY = 0;
                  currWidth = 0;
                  currHeight = 0;
                  resultingContent = resultingContent + "\n\t},\n\t{";
                  resultingContent = resultingContent + enter() + tab() + tab() + quote() + "filename" + quote() + " : " + quote() + line + quote() + ",";
                }
            }

            if (currWidth != 0 && currHeight != 0) 
            {
                resultingContent = resultingContent + addFrame(currOriginX, currOriginY, currWidth, currHeight);
                resultingContent = resultingContent + addSpriteSourceSize(currWidth, currHeight);
                resultingContent = resultingContent + addSourceSize(currWidth, currHeight);
            }

            resultingContent = resultingContent + "\n\t}\n]}";
            file.close();
            return resultingContent;
      }
      catch (Exception e) 
      {
           System.err.format("Exception ocurred when trying to read '%s'", filename);
           e.printStackTrace();
           pressEnterToExit();
           return "";
      }
   }

   public static boolean fileCreation(String content, String filename, String path) 
   {
      try 
      {
         PrintWriter file;
         if(filename.contains("/") || filename.contains("\\"))
            file = new PrintWriter(filename);
         else
         {
            file = new PrintWriter((path + filename));
         }
         System.out.println("Creating file '" + filename + "' at path: " + path);
         file.write(content);
         file.close();
         return true;
      } 
      catch (Exception e) 
      {
         System.out.println("Could not write file " + filename);
         e.printStackTrace();
         pressEnterToExit();
         return false;
      }
   }

   public static boolean fileCreation(String content, String filename) {return fileCreation(content, filename, "");}

   public static void pressEnterToExit() 
   {
      System.out.println("Press enter to exit");

      try 
      {
         System.in.read();
      }
      catch (Exception e) 
      {
         e.printStackTrace();
      }

      System.exit(0);
   }

   public static void main(String[] args) 
   {
        if(args.length == 0)
        {
            String filename = checkExceptions(args);
            filename = CrossPlatformFunctions.crossPlatformSelect("*.atlas");
            if(filename == "" || filename == null)
            {
               System.out.println("Please specify the input after calling AtlasToJSON");
               System.exit(-1);  
            }
            String outputFolder = CrossPlatformFunctions.crossPlatformSave(filename.replaceFirst(".atlas", ".json"));
            if(outputFolder == null || outputFolder == "")
            {
               System.out.println("Please specify a directory for saving the output");               
               System.exit(-1);
            }
            
            String newFileName = getFileName(outputFolder);
            String resultFolder = getPath(outputFolder);
            System.out.println(filename);
            String fileContent = fileRead(filename);

            if (fileContent != "") 
                fileCreation(fileContent, newFileName, resultFolder);

        }
        else
        {
            String outputFolder = "";
            String filename = checkExceptions(args);
            if(filename == "")
            {
               System.out.println("Please specify the input after calling AtlasToJSON");
               System.exit(-1);  
            }
            boolean willTryToOpenExplorer = true;
            if (args.length == 3 && args[2].contains("-i")) 
            willTryToOpenExplorer = false;
    
            if (args.length <= 1) 
            {
                System.out.println("No output folder detected, the output dir will be the current directory");
                outputFolder = System.getProperty("user.dir");
            }
            else 
                outputFolder = args[1];
    
            String jsonFile = filename.replaceFirst(".atlas", ".json");
    
            if (checkFileExistence(outputFolder + '\\' + jsonFile) && JOptionPane.showConfirmDialog(null, "File named '" + jsonFile + "' already exists in path: " + outputFolder + "\\\nDo you want to overwrite?") != JOptionPane.YES_OPTION) 
            {
                System.out.println("Stopping current process");
                System.exit(-1);
            }
            String fileContent = fileRead(filename);
            if (fileContent != "") 
                fileCreation(fileContent, jsonFile, outputFolder + '\\');
            if (willTryToOpenExplorer) 
                CrossPlatformFunctions.openCurrentSystemExplorer(outputFolder + '\\');
            if (!willTryToOpenExplorer) 
                pressEnterToExit();
          
        }
        System.exit(0);
   }
}