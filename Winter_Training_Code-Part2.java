package Exceltojava;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import java.lang.*;
import java.util.*;
import org.leores.plot.JGnuplot;
import org.leores.util.data.DataTableSet;
//import readapache.row;
/**
 *
 * @author Jayeesha,Riha,Akansha,Moushree,Bishal, Abhay, Vishesh, Rajdeep, Alok, Bidhusi, Ashish
 */
public class readcolumn {
    
    static int d=0;
    static double logfq[]=new double[18];
    static int s;
//step1 calculate log(i+1)-log(i)
    static void step1(HSSFCell cell_value[],int length,HSSFCell originalData[],int rows)
    {
        double b[]=new double[length];
        for(int i=0;i<length;i++)
        {
            float number=Float.parseFloat(cell_value[i].getStringCellValue());
            float nextNumber=Float.parseFloat(cell_value[i+1].getStringCellValue());
            b[i]=Math.log(nextNumber)-Math.log(number);
        }
         step1part2(originalData,rows,b,length); // function call to Calculate mean and  log(i+1)-log(i)/mean 
    }
    //Calculate mean and  log(i+1)-log(i)/standard deviation
    static void step1part2(HSSFCell original_Data[],int rows,double g[],int length)
    {
        float mean,sum=0,data,minusMean,sumsq=0,meansq,sd,sumofg=0;
        double meang;
        double profile[]=new double[length];
        for(int k=0;k<rows;k++)
        {
           data=Float.parseFloat(original_Data[k].getStringCellValue());
           sum+=data;
        }
        mean=sum/rows;
        for(int p=0;p<rows;p++)
        {
               minusMean=Float.parseFloat(original_Data[p].getStringCellValue())-mean;
               minusMean=minusMean*minusMean;
               sumsq+=minusMean;
        }
        meansq=sumsq/rows;
        sd=(float)Math.sqrt(meansq);//calculate standard devieation
        //mean of g(t) i.e <g>
        for(int q=0;q<length;q++)
        {
            g[q]=g[q]/sd;
            sumofg+=g[q];
        }
        meang=sumofg/rows;
        profile[0]=g[0]-meang;
        //profile i.e y(i)
        for(int i=1;i<length;i++)
        {
            g[i]=g[i]-meang;
            profile[i]=profile[i-1]+g[i];
        }
       step2n3(profile,g,length);
    }
    //calculate Ns and fitting curve calling
        static void step2n3(double profile[],double g[],int length)
        {
            //step 1 
            //int s;
            for( s=2;s<20;s++)
            {
            int ns=length/s;//s=2
            System.out.println("ns= "+ns);
            //step 3 started ..
          //fittng curve calling
            SquareMethod(g,profile,length,ns);
            }
            //designing graph
            plot2d();
        }
        
        static void SquareMethod(double[] x, double[] y,int n,int ns) 
         {
             double yv[]=new double[n];
             double sumx=0,sumx2=0,sumy=0,sumxy=0,xbar,ybar;
            for (int i = 0; i < n; i++) 
            {
                sumx  += x[i];
                sumx2 += x[i]*x[i];
                sumy  += y[i];
                sumxy += x[i]*y[i];
            }
            xbar = sumx / n;
            ybar = sumy / n;
        // fitting the equation in a straight line
        // finding the value of a 
            double a,t1,t2;
            t1=(ybar*sumx2)-(xbar*sumxy);
            t2=sumx2-(n*xbar*xbar);
            a=t1/t2;
            //finding the value of b
            double b,t3;
            t3=sumxy-(n*xbar*ybar);
            b=t3/t2;
            //equation of the fitting curve
            System.out.println("The eq is:y="+a+"x+("+b+")"); 
         
             // input value in y(i)
             for(int i=0;i<n;i++)
             {
                 yv[i]=a*x[i]+b;
             }
             step3part2(y,n,ns,yv);
         }
        //Calculating f(s,v) forward backward
     static void step3part2(double y[],int length,int ns,double[] yv)
        {
            int k=0;
            double sum=0,sum1=0,f2,f3,finalsum;
            double F1[]=new double[length];
            double F2[]=new double[length];
            //forward
            for(int v=1;v<ns;v++)
            {
                for(int i=0;i<2;i++)
                {
                    F1[k]=y[(v-1)*2+i]-yv[i];
                    k++;
                }
            }
            for(int i=0;i<length;i++)
            {
                sum+=Math.pow(F1[i], 2);
            }
            f2=sum/s;
            k=0;
            //backward
            for(int v=ns+1;v<2*ns;v++)
            {
                for(int i=1;i<=2;i++)
                {
                    F2[k]=y[length-((v-ns)*2+i)]-yv[i];
                    k++;
                }
            }
            for(int i=0;i<length;i++)
            {
                sum1+=Math.pow(F2[i], 2);
            }
            f3=sum1/s;
            finalsum=f2+f3;
            System.out.println("final sum"+finalsum);
            //Calculating fq for results
            double q;
            q=3;
            double x=Math.pow(finalsum,(q/2));
            double fq[]=new double[18];
            fq[d]=Math.pow((x/(2*ns)),(1/q));
            System.out.println("fq value "+fq[d]+"d= "+d); 
            logfq[d]=Math.log(fq[d]);
            System.out.println("d= "+d+ "logfq="+logfq[d]); 
            d++;
     }
       //Obtaining graphs 
     public static void plot2d()
     {
        double logs[]=new double[18];
        for(int s=2;s<20;s++)
        {
            logs[s-2]=Math.log(s);
            System.out.println("s= "+s+"log s "+logs[s-2]); 

         }
                JGnuplot jg = new JGnuplot();
		JGnuplot.Plot plot = new JGnuplot.Plot("") {
			{
				xlabel = "log s";
				ylabel = "log fq";
			}
		};
		
		DataTableSet dts = plot.addNewDataTableSet("2D Plot");
		dts.addNewDataTable("",logs, logfq);
                jg.compile(plot, jg.plot2d);
                jg.terminal = null;//Set the terminal to the default terminal
                jg.terminal = "jpeg enhanced size 600,600";
		jg.output = "plot1.jpg";
                jg.execute(plot, jg.plot2d);
        }
    
    public static void main(String[] args) throws Exception
{
    FileInputStream fs = new FileInputStream(new File("C:\\Users\\Moushree\\Desktop\\S_set-1.xls"));
    HSSFWorkbook wb = new HSSFWorkbook(fs);
    HSSFSheet sheet = wb.getSheetAt(0);
    HSSFRow row,rowComp;
    HSSFCell cell,compare;
    int rows; // No of rows
    rows = sheet.getPhysicalNumberOfRows();
    HSSFCell a[]=new HSSFCell[rows];
    HSSFCell originalData[]=new HSSFCell[rows];
    row=sheet.getRow(0);
    rowComp = sheet.getRow(6);
    compare=rowComp.getCell((short)1);
    int j=0,k=0;
    Iterator < Row > rowIterator = sheet.iterator();
    DataFormatter df = new DataFormatter();
    while (rowIterator.hasNext())
    {
        cell = row.getCell((short)1);
        if(cell!=null)
        {
            originalData[k]=cell;
            if (df.formatCellValue(cell).equals(df.formatCellValue(compare)))
            { 
            } 
            else
            {//all on zero values
                            a[j]=cell;
                            System.out.println(a[j]);
                            j++;
            }
          k++;
        }
        row = (HSSFRow) rowIterator.next();              
    }
    System.out.println("no. of rows"+k);    
    int lengthA=j-1;
    step1(a,lengthA,originalData,k);
 }  
        
}



