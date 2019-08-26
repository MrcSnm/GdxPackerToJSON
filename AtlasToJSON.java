import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class AtlasToJSON 
{
   public static boolean checkFileExistence(String filename) 
   {
      File f = new File(filename);
      return f.exists();
   }

   public static void openCurrentSystemExplorer(String path) 
   {
      if ( (JOptionPane.showConfirmDialog(null, "Do you want to open system file explorer?", "Open system file explorer", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)!= JOptionPane.YES_OPTION))
         return;
      String os = System.getProperty("os.name").toLowerCase();
      String command = "";
      if (os.contains("mac") || os.contains("darwin")) 
         command = "open " + path;
      else if (os.contains("win")) 
         command = "Explorer.exe " + path;
      else if (os.contains("nux") || os.contains("nix")) 
         command = "xdg-open " + path;
      else
      {
         System.out.println("Opening file explorer not supported on your system");
         return;
      }

      try 
      {
         Runtime.getRuntime().exec(command);
      }
      catch (Exception e) 
      {
         JOptionPane.showConfirmDialog((Component)null, "The command " + command + " is not supported on your system", "Command not supported", 0, 0);
         e.printStackTrace();
      }
   }

   public static String checkExceptions(String[] args) 
   {
      int argc = args.length;
      String filename = args[0];
      if (argc == 0) 
      {
         System.out.println("No input file detected, please specify the input after calling AttlasToJSON");
         return "";
      } 
      else if (argc > 3) 
      {
         System.out.println("Too many arguments, ignoring procedure");
         return "";
      } 
      else 
      {
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
      char[] newStr = new char[fileDir.length()];
      char[] buffer = fileDir.toCharArray();
      int counter = 0;

      for(int currentCounter = 0; counter != buffer.length; ++counter) 
      {
         newStr[currentCounter] = buffer[counter];
         ++currentCounter;
         if (buffer[counter] == '\\' || buffer[counter] == '/') 
         {
            newStr = new char[buffer.length - counter];
            currentCounter = 0;
         }
      }

      return String.copyValueOf(newStr);
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
            file = new PrintWriter(path + filename);
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
        String filename = checkExceptions(args);
        if(filename == "")
            return;
        boolean willTryToOpenExplorer = true;
        if (args.length == 3 && args[2].contains("-i")) 
        willTryToOpenExplorer = false;

        String outputFolder;
        if (args.length == 1) 
        {
            System.out.println("No output folder detected, output will be in current folder");
            outputFolder = System.getProperty("user.dir");
        }
        else 
        outputFolder = args[1];

        String jsonFile = filename.replaceFirst(".atlas", ".json");
        if (checkFileExistence(outputFolder + '\\' + jsonFile) && JOptionPane.showConfirmDialog(null, "File named '" + jsonFile + "' already exists in path: " + outputFolder + "\\\nDo you want to overwrite?") != JOptionPane.YES_OPTION) 
        {
            System.out.println("Stopping current process");
            return;
        }
        String fileContent = fileRead(filename);
        if (fileContent != "") 
            fileCreation(fileContent, jsonFile, outputFolder + '\\');
        if (willTryToOpenExplorer) 
            openCurrentSystemExplorer(outputFolder + '\\');
        if (!willTryToOpenExplorer) 
            pressEnterToExit();
      
   }
}