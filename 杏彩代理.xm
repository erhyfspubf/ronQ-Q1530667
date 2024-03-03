<?xml version="1.0"?>
<!-- ====================================================================== 
     2005/08/05 16:47:07                                                        

     project    
     description
                   
     Administrator                                                                
     ====================================================================== -->
<project name="project" default="default">
    <description>
            description
    </description>
	<property name="src" value="src"/>
	<property name="bin" value="bin"/>
	<property name="javadoc" value="doc"/>
	<property name="outputjar" value="qrxmlsocket.jar"/>
	<property name="manifest" value="manifest.mf"/>


    <!-- ================================= 
          target: default              
         ================================= -->
    <target name="default" depends="compile,jar,javadoc">
    </target>
	

    <!-- ================================= 
          target: javadoc              
         ================================= -->
    <target name="javadoc">
		<javadoc 
    		sourcepath="${src}" 
    		destdir="${javadoc}"
			packagenames="org.qrone.*" 
			excludepackagenames="org.qrone,org.qrone.sample">
			
			<doctitle><![CDATA[<h1>QrXMLSocket Library</h1>]]></doctitle>
    		<bottom><![CDATA[<i>Copyright &#169; 2005 J.Tabuchi All Rights Reserved.</i>]]></bottom>

		</javadoc>
    </target>
    	
    <!-- ================================= 
          target: jar              
         ================================= -->
    <target name="jar" depends="compile">
    	<ant antfile="../QrTools/build.xml" target="compile"/>
		<jar destfile="${bin}/${outputjar}" manifest="manifest.mf">
		    <fileset dir="${bin}"/>
		    <fileset dir="../QrTools/${bin}"/>
		</jar>
    </target>

    <!-- ================================= 
          target: clean              
         ================================= -->
    <target name="clean">
		<delete file="${bin}/${outputjar}"/>
		<delete dir="${javadoc}"/>
    </target>

    <!-- - - - - - - - - - - - - - - - - - 
          target: compile                      
         - - - - - - - - - - - - - - - - - -->
    <target name="compile" depends="clean">
    	<mkdir dir="${bin}"/>
        <javac srcdir="${src}"
         destdir="${bin}"
         classpath="./"
         debug="on"
	/>
    </target>
</project>

