# Alfresco CAD (dxf, dwg) preview support
This project includes Alfresco (4.x,5.x) config files that in conjunction with QCAD software provide DWG/DXF engineering documents preview within your Alfresco Share. Works in Linux and in Windows.

1. Download [trial](http://www.qcad.org/en/qcad-downloads-trial) QCAD converter to some dir (e.g. "/opt/qcad"). This software will perform a dxf/dwg to pdf transformation required for workable preview in Alfresco. Yes it's trial and that's why it takes extra 15 seconds before conversion starts. But it's the best solution I've found.
2. Build current project using Apache Ant. It will generate a /%project_dir%/build/alf-cad-support.jar file that should be deployed to your alfresco webapp instance: %tomcat_dir%/webapps/alfresco/WEB-INF/lib/.
3. Add to you alfresco-global.properties:
>dwg2pdf.root=/opt/qcad
>
>content.transformer.dwg2pdf.priority=50
>
>content.transformer.dwg2pdf.extensions.dwg.pdf.supported=true
>content.transformer.dwg2pdf.extensions.dwg.pdf.priority=50
>
>content.transformer.dxf2pdf.priority=50
>
>content.transformer.dxf2pdf.extensions.dxf.pdf.supported=true
>content.transformer.dxf2pdf.extensions.dxf.pdf.priority=50

4. Restart Tomcat

Now DXF/DWG preview works on your Alfresco. More details can be found in my blog [post].
