"""
Autopsy Forensic Browser

Copyright 2019 Basis Technology Corp.
Contact: carrier <at> sleuthkit <dot> org

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

from java.io import File
from java.lang import Class
from java.lang import ClassNotFoundException
from java.lang import Long
from java.lang import String
from java.sql import ResultSet
from java.sql import SQLException
from java.sql import Statement
from java.util.logging import Level
from java.util import ArrayList
from org.apache.commons.codec.binary import Base64
from org.sleuthkit.autopsy.casemodule import Case
from org.sleuthkit.autopsy.coreutils import Logger
from org.sleuthkit.autopsy.coreutils import MessageNotifyUtil
from org.sleuthkit.autopsy.coreutils import AppSQLiteDB
from org.sleuthkit.autopsy.coreutils import AppDBParserHelper
from org.sleuthkit.autopsy.datamodel import ContentUtils
from org.sleuthkit.autopsy.ingest import IngestJobContext
from org.sleuthkit.datamodel import AbstractFile
from org.sleuthkit.datamodel import BlackboardArtifact
from org.sleuthkit.datamodel import BlackboardAttribute
from org.sleuthkit.datamodel import Content
from org.sleuthkit.datamodel import TskCoreException
from org.sleuthkit.datamodel import Account

import traceback
import general

"""
Finds the SQLite DB for Zapya, parses the DB for contacts & messages,
and adds artifacts to the case.
"""
class ZapyaAnalyzer(general.AndroidComponentAnalyzer):

    moduleName = "Zapya Analyzer"
    progName = "Zapya"
    
    def __init__(self):
        self._logger = Logger.getLogger(self.__class__.__name__)

    def analyze(self, dataSource, fileManager, context):
        selfAccountId = None
        transferDbs = AppSQLiteDB.findAppDatabases(dataSource, "transfer20.db", "com.dewmobile.kuaiya.play")
        for transferDb in transferDbs:
            try:
                transferDbHelper = AppDBParserHelper(self.moduleName, transferDb.getDBFile(),
                                                    Account.Type.ZAPYA)

                queryString = "SELECT device, name, direction, createtime, path, title FROM transfer"
                transfersResultSet = transferDb.runQuery(queryString)
                if transfersResultSet is not None:
                    while transfersResultSet.next():
                        direction = ""
                        fromAddress = None
                        toAdddress = None
                        otherAccountId = None
                        if (transfersResultSet.getInt("direction") == 1):
                            direction = "Outgoing"
                            toAddress = "{0} ({1})".format(transfersResultSet.getString("name"), transfersResultSet.getString("device") )
                            otherAccountId = toAddress
                        else:
                            direction = "Incoming"
                            fromAddress = "{0} ({1})".format(transfersResultSet.getString("name"), transfersResultSet.getString("device") )
                            otherAccountId = fromAddress
                                                
                        timeStamp = transfersResultSet.getLong("createtime") / 1000
                        messageArtifact = transferDbHelper.addMessage( 
                                                            otherAccountId,
                                                            "Zapya Message",
                                                            direction,
                                                            fromAddress,
                                                            toAddress,
                                                            timeStamp,
                                                            -1,     # read status
                                                            transfersResultSet.getString("title"), 
                                                            transfersResultSet.getString("path"),
                                                            "" )
                                                                                                
                        # TBD: add the file as attachment ??

            except SQLException as ex:
                self._logger.log(Level.SEVERE, "Error processing query result for transfer", ex)       
            finally:
                transferDb.close()
                

    
