// CSE 142, Homework 7 (DNA)
// This helper program can read NCBI genetic data files with
// .fna and .ptt extensions and preprocess them to create input
// files suitable for use with the HW7 Dna program.

import java.io.*;
import java.util.*;
import java.util.Scanner;

public class ConvertDna {
    // % of characters to randomly make lowercase
    public static final int PERCENT_LOWERCASE = 10;
    
    // % of lines to have as proteins (the rest are random DNA)
    public static final int PERCENT_PROTEINS = 66;
    
    
    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        
        System.out.print("Genome (.fna) file? ");
        Scanner fnaInput = new Scanner(new File(console.nextLine()));
        System.out.println("Reading .fna file...");
        StringBuilder sb = getGenome(fnaInput);

        System.out.print("Protein (.ptt) file? ");
        Scanner pttInput = new Scanner(new File(console.nextLine()));
        
        System.out.print("Output file? ");
        PrintStream out = new PrintStream(new File(console.nextLine()));
        
        System.out.print("How many proteins (-1 for all)? ");
        int proteins = console.nextInt();
        System.out.println("Producing protein output...");
        
        readProtein(pttInput, proteins, sb, out);
    }
    
    public static StringBuilder getGenome(Scanner fnaInput) {
        StringBuilder sb = new StringBuilder(4000000);
        fnaInput.nextLine(); // skip header
        while (fnaInput.hasNextLine()) {
            sb.append(fnaInput.nextLine());
        }
        return sb;
    }
    
    public static void readProtein(Scanner pttInput, int proteins, 
            StringBuilder sb, PrintStream out) {
        pttInput.nextLine();   // skip header lines
        pttInput.nextLine();
        pttInput.nextLine();
        
        Random rand = new Random(42);
        while (proteins != 0 && pttInput.hasNextLine()) {
            String line = pttInput.nextLine();
            Scanner lineScan = new Scanner(line);
            lineScan.useDelimiter("[ \t\n\f\r:.]+");
            int start = lineScan.nextInt();
            int end = lineScan.nextInt();
            String strand = lineScan.next();  // "+" or "-"
            
            if (strand.equals("+")) {
                lineScan.next();  // skip length token
                lineScan.next();  // skip PID token
                lineScan.next();  // skip gene token
                lineScan.next();  // skip synonym token
                lineScan.next();  // skip code token
                lineScan.next();  // skip COG token
                String name = lineScan.next();
                while (lineScan.hasNext()) {
                    name += " " + lineScan.next();
                }

                if (rand.nextInt(100) > PERCENT_PROTEINS) {
                    // grab some random dna 
                    start = rand.nextInt(sb.length() - 3) + 1;
                    end = Math.min(start + 3 + 3 * rand.nextInt(100), sb.length()) - 1;
                    name = "Non-protein region";
                }
                int length = end - start + 1;

                out.println(name);

                StringBuilder range = new StringBuilder(sb.substring(start - 1, end));

                // pseudo-randomly change casing of 10% of nucleotides
                for (int i = 0; i < length * PERCENT_LOWERCASE / 100; i++) {
                    int index = rand.nextInt(length);
                    range.setCharAt(index, Character.toLowerCase(range.charAt(index)));
                }
                out.println(range);
                proteins--;
            }
        }
    }
}