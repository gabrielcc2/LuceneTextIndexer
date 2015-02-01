/*THIndexer class (Text and HTML Indexer):
 * This class implements the functionality of a text and html indexer. As such, it allows 2 phases: Building an index,
 * over a given browser, and then, Searching over this index.
 * 
 * It is composed of the following functions:
 * 
 * A) Basic functions:
 * 	1. Default constructor: public THIndexer()
 *  2. GUI initializer: private void initComponents() 
 * 
 * B) Functions used in the Build Index phase:
 * 	3. Action listener for the Browse button: private void browseBtnActionPerformed(java.awt.event.ActionEvent) 
 *  4. Action listener for the Build Index button: private void buildIndexBtnActionPerformed(java.awt.event.ActionEvent)
 *  5. Function in charge of adding files to the index: private boolean addFileToIndex(File, IndexWriter)
 * 
 * C) Functions used in the Search phase:
 *  6. Action listener for the Search button, implements the searching functionality of the indexer: private void searchBtnActionPerformed(java.awt.event.ActionEvent)
 *  7. main: public static void main(String args[])
 * 
 * Multimedia Retrieval Programming Assignment #1/
 * Shadi Akhras, Gabriel Campero
 * SoSe2014, OvGU Magdeburg*/

import java.io.*;
import javax.swing.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;

public class THIndexer extends javax.swing.JFrame { //Main class
    
	//Declaration of class variables
	private File chosenDirectory; //Java IO class, for the directory that will be indexed.
	private int fileIndexedNum = 0; //Number of indexed files.
	
	//Lucene variables
	private static Analyzer analyzer; //Lucene variable, implementing text analysis functionality, such as tokenizing and the porter stemmer algorithm. 
	private static FSDirectory index;//Lucene variable for managing the directory where the index will be stored. By referring to this folder we can find the index.
	private static IndexWriter writer;//Lucene variable allowing to add files to the index.
	private static IndexWriterConfig config; //Lucene variable allowing to configure properties of the index. One of this properties is the analyzer used. 
	
	
	//GUI related variables
	private javax.swing.JLabel IndexingResultsLab;
	private javax.swing.JButton browseBtn;
	private javax.swing.JButton buildIndexBtn;
	private javax.swing.JFileChooser jFileChooser1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JLabel pathLab;
	private javax.swing.JLabel resultsNumLab;
	private javax.swing.JButton searchBtn;
	private javax.swing.JTextField searchTxt;
	private javax.swing.JLabel title1Lab;
	private javax.swing.JLabel title2Lab;
	private javax.swing.JTextArea resultsTextArea;
	private static final long serialVersionUID = 1L; //Version Id, used by the GUI. 
	//End of declaration of class variables
	
	public THIndexer() {//The constructor only initializes the interface. 
        initComponents(); 
    }
   
	private void initComponents() {//Initializes the interface. Only called by the constructor.
		//Initialize the JFrame
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Text File Indexer");
		//Initialize the Interface Components
        jFileChooser1 = new javax.swing.JFileChooser();
        browseBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        pathLab = new javax.swing.JLabel();
        buildIndexBtn = new javax.swing.JButton();
        IndexingResultsLab = new javax.swing.JLabel();
        title1Lab = new javax.swing.JLabel();
        title2Lab = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        searchTxt = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        resultsNumLab = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();        
        resultsTextArea = new javax.swing.JTextArea();
                
        resultsTextArea.setRows(10);
        resultsTextArea.setEditable(false);
        resultsTextArea.setEnabled(false);
        searchBtn.setText("Search");
        searchBtn.setEnabled(false);
        searchTxt.setEnabled(false);
        jFileChooser1.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        jLabel1.setText("Choose the folder which contains the files you want to index");
        pathLab.setText("");        
        buildIndexBtn.setText("Build Index");
        browseBtn.setText("Browse");
        IndexingResultsLab.setText("");
        title1Lab.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        title1Lab.setText("Build Index");
        title2Lab.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        title2Lab.setText("Search");
        title2Lab.setEnabled(false);        
        resultsNumLab.setText("");        
        jScrollPane1.setViewportView(resultsTextArea); 
        
        //Adding the ActionListener functions
        browseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseBtnActionPerformed(evt);
            }
        });
        buildIndexBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
					buildIndexBtnActionPerformed(evt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
					searchBtnActionPerformed(evt);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
              
        //Drawing the interface
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                    .addComponent(title1Lab)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(browseBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buildIndexBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pathLab)
                            .addComponent(IndexingResultsLab)))
                    .addComponent(title2Lab)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchBtn))
                    .addComponent(resultsNumLab)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title1Lab)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browseBtn)
                    .addComponent(pathLab))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buildIndexBtn)
                    .addComponent(IndexingResultsLab))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(title2Lab)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultsNumLab)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );       

        pack();
    }
    
	//Functions for the Build index phase:	
	private void browseBtnActionPerformed(java.awt.event.ActionEvent evt) { //Browswing action listener      
        int browseResult = jFileChooser1.showOpenDialog(null);
        if (browseResult == JFileChooser.APPROVE_OPTION) {
            this.chosenDirectory = jFileChooser1.getSelectedFile();
            pathLab.setText(chosenDirectory.toString());
        }
    }

    private void buildIndexBtnActionPerformed(java.awt.event.ActionEvent evt) throws IOException { //Index button action listener (for building the index)
    	//At first we initialize the global variables used for managing the indexer.
    	analyzer = new EnglishAnalyzer(Version.LUCENE_48); //Note: This implementation uses the EnglishAnalyzer.
	    index  = FSDirectory.open(new File("./index")); //The indexer will be stored on the index subfolder of the current directory.
		config = new IndexWriterConfig(Version.LUCENE_48, analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		writer = new IndexWriter(index, config); 
		fileIndexedNum = 0;
		
		if(addFileToIndex(chosenDirectory)){ //If it's true the index is built.
			
			IndexingResultsLab.setText("Index built successfully. Number of indexed file(s): "+fileIndexedNum);
			//The following GUI related functions enable the search button once the index has been created.
			searchBtn.setEnabled(true); 
	        title2Lab.setEnabled(true);        
	        resultsTextArea.setEnabled(true);
	        searchTxt.setEnabled(true);
		}
		else {
			IndexingResultsLab.setText("Please Choose a directory!");			
		}
		 writer.close();
    }
	    
    private boolean addFileToIndex(File f) throws IOException {/*This function is called by the Index button action listener, 
    and is in charge of browsing the directory recursively (so as to include sub-folders), 
    and then it adds each found file to the Lucerne IndexWriter passed as parameter.
    Note: It only adds html, htm and txt files.
    It returns true if the index was successfully built, and false if not.*/
		if (f.exists()) { //For each file or folder
			if (f.isDirectory()) { //It checks if it is a directory (i.e. a folder)
				File[] files = f.listFiles(); //In this case we create an array with all the files and directories within the current folder.
				//Now it iterates over each element in the array.
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						 addFileToIndex(files[i]); //If the element is a directory, the function calls itself recursively over this sub-folder.
					} else if (files[i].isFile()) {//If the element is a file, it is indexed according to it's type.
						Document doc = new Document(); //The document representation that will be added to the indexer.
						if (files[i].getName().endsWith(".txt")) {//If it's a txt file.
							FileReader fr = new FileReader(files[i]); //Java.io file reader over the file.
							//Now it creates 3 fields for the given document: body, path and filename.
							doc.add(new TextField("body", fr));
							doc.add(new StringField("path", files[i].getPath(),Field.Store.YES));
							doc.add(new StringField("filename", files[i].getName(), Field.Store.YES));
							writer.addDocument(doc); //Finally the document is added to the Lucene IndexWriter.
							fileIndexedNum++; //The global variable is incremented.
						} else if (files[i].getName().endsWith(".html") //For html files, the same is done, but it also stores the title of the page.
								|| files[i].getName().endsWith(".htm")) {
							org.jsoup.nodes.Document htmlDoc = Jsoup.parse(files[i], "UTF-8");//Note that instead of using the file reader we use the jsoup HTML file parser.
							String title = htmlDoc.title();
							doc.add(new TextField("body",htmlDoc.body().text(), Field.Store.YES));
							doc.add(new StringField("path", files[i].getPath(),Field.Store.YES));
							doc.add(new StringField("filename", files[i].getName(), Field.Store.YES));
							doc.add(new TextField("title", title,Field.Store.YES));
							writer.addDocument(doc); //Finally the document is added to the Lucene IndexWriter.
 							fileIndexedNum++;
						}						
					}
				}
				return true; //Files were added.
			} else {
				return false; //No files were added (if a file instead of a folder is passed by the user).
			}
		} else {
			return false; //No files were added since the folder is not accessible or doesn't exist.
		}		
	}

  //Functions for the Search phase:

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) throws ParseException, IOException { //Search button action listener (for performing searches)
		String querystr = searchTxt.getText();
		Query q = new QueryParser(Version.LUCENE_48, "body", analyzer).parse(querystr); //The query will contain the parsed phrase that the user submitted.
		IndexReader reader = DirectoryReader.open(index); //Lucene variable allowing to read an index (load it from storage)
		IndexSearcher searcher = new IndexSearcher(reader); //Lucene variable allowing to search over an index.
		int hitsPerPage = 100;
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true); //Lucene variable allowing to collect the top results for the search, according to the default scorer.
		//Note: According to Lucene's documentation, the default scorer uses the Vector Space model of Information Retrieval to determine the revlevance of a given document for the query.
		//The main idea behind this, is of course, inverse frequency. Some refinements are then added to this basic model.
		searcher.search(q, collector); //The query is performed over the index.
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		resultsNumLab.setText("Number of results: "+hits.length);
		resultsTextArea.setText("");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			if (d.get("filename").endsWith(".html")|| d.get("filename").endsWith(".htm")) {				
				resultsTextArea.append(
						(i + 1) + ". " 
						+ "File Name: " + d.get("filename") 
						+ "\n File Path: " + d.get("path")
						+ "\n File Type: HTML" 
						+ "\n HTML File Title:" + d.get("title") 
						+ " \n Scoring for the document: " + searcher.explain(q, docId));
			} else {				
				resultsTextArea.append(
						(i + 1) + ". " 
						+ "File Name: " + d.get("filename") 
						+ "\n File Path: " + d.get("path")
						+ "\n File Type: Text " 
						+ " \n Scoring for the document: " + searcher.explain(q, docId));
			}
			resultsTextArea.append("\n----------------------------------------------------------\n");
		}
		reader.close();
	}
    
  //Main function
    public static void main(String args[]) {        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new THIndexer().setVisible(true);
            }
        });
    }     

}
