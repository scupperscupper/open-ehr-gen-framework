/*
Copyright 2013 CaboLabs.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This software was developed by Pablo Pazos at CaboLabs.com

This software uses the openEHR Java Ref Impl developed by Rong Chen
http://www.openehr.org/wiki/display/projects/Java+Project+Download

This software uses MySQL Connector for Java developed by Oracle
http://dev.mysql.com/downloads/connector/j/

This software uses PostgreSQL JDBC Connector developed by Posrgresql.org
http://jdbc.postgresql.org/

This software uses XStream library developed by Jörg Schaible
http://xstream.codehaus.org/
*/
package hce.core.composition.content.entry

import hce.core.datastructure.itemstructure.ItemStructure
import data_types.quantity.date_time.DvDateTime

// TEST: para serializar DataValue a string y ahorrar joins
import com.thoughtworks.xstream.XStream
import data_types.basic.DataValue

class Action extends CareEntry {

    DvDateTime time
    ItemStructure description
    //ISMTransition ismTransition
    //InstructionDetails instructionDetails

    String codedTime // Nuevo: donde guardar el string
    static transients = ['time'] // Nuevo: no quiero que se guarde value, quiero guardar codedValue
    
    // Nuevo para calcular codedValue
    def beforeInsert() {
       // Para generar XML en una sola linea sin pretty print: http://stackoverflow.com/questions/894625/how-to-disable-pretty-printingwhite-space-newline-in-xstream
       // Interesante: http://www.xml.com/pub/a/2001/06/20/databases.html
       XStream xstream = new XStream()
       xstream.omitField(DataValue.class, "errors");
       codedTime = xstream.toXML(time)
       codedName = xstream.toXML(name) // atributo de locatable
    }
    def beforeUpdate() {
       XStream xstream = new XStream()
       xstream.omitField(DataValue.class, "errors");
       codedTime = xstream.toXML(time)
       codedName = xstream.toXML(name) // atributo de locatable
    }
    // Al reves
    def afterLoad() {
       XStream xstream = new XStream()
       xstream.omitField(DataValue.class, "errors");
       if (codedTime) time = xstream.fromXML(codedTime)
       if (codedName) name = xstream.fromXML(codedName)
    }
    
    static mapping = {
        //time cascade: "save-update"
        description cascade: "save-update"
        description column: "action_description_id"
    }

    static constraints = {
        //time (nullable: false)
        description (nullable: false)
        //ismTransition (nullable: false)
        codedTime (nullable: true, maxSize:4096)
    }
}