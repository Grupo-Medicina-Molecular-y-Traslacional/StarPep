# StarPep toolbox
Welcome to the _StarPep toolbox_ project GitHub repository. Here is where all the components of the project are developed, reviewed, and maintained.

<p align="aligncenter">
    <img src="img/StarPep_logo.png" alt="StarPep Logo" style="height: width:800px;"/>
</p>

**Table of contents:**

- [About the project](#about-the-project)
- [Installation](#install)
- [The Team](#the-team)
- [Contributing](#contributing)
- [Get in touch](#get-in-touch)

## About the Project
[StarPep toolbox](http://mobiosd-hub.com/starpep/) is a software for studying the antimicrobial peptides' (AMPs) chemical space with molecular network-based representations and similarity searching models. This application aims to contribute to peptide drug repurposing, development, and optimization. 

This tool was developed as a Java desktop application that integrates the functionalities of several open-source projects. The graphical user interface was built on top of the [NetBeans Platform](https://platform.netbeans.org/), using the Java SE Runtime Environment 8. The graph database structure was implemented with the [Neo4j](https://neo4j.com/) platform. Some visualization features and the calculation of network properties were based on [Gephi](https://gephi.org/). The sequence alignment algorithms were implemented using the [BioJava](https://biojava.org/) API. 

The AMPs were collected from a large variety of biological data sources to be organized into an integrated graph database called
[starPepDB](https://doi.org/10.1093/bioinformatics/btz260), composed of 45.120 AMPs and their metadata. This integrated graph database is
embedded in StarPep toolbox to enable end-user querying, filtering, visualizing, and analyzing the AMPs taking advantage of network-based representations.

The main features of StarPep toolbox are listed below:

* **AMPs' chemical space filtering:** obtain a subset of AMPs from the StarPepDB using their metadata (function, target pathogen, biological origin, chemical modifications, original database, and cross-referenced entries to PDB, PubMed, and UniProt).

* **Molecular descriptors:** calculate molecular descriptors of the AMPs by applying statistical and aggregation operators on physicochemical amino acid properties (e.g., net charge, isoelectric point, molecular weight, etc.).

* **Network Science:** build different types of networks (metadata, chemical space, and half-space proximal) and calculate global/local properties, centrality metrics, communities, etc.

* **Similarity searching:** create multi-query similarity searching models that can lead to the repurposing of AMPs with novel functional activities.

For more details about the software and its features, read the [online User Guide](https://grupo-medicina-molecular-y-traslacional.github.io/StarPep_doc/). 

## Install and use StarPep toolbox
The binary executable files for Windows, Mac, and Linux are located in http://mobiosd-hub.com/starpep/. You can download the zip distribution and extract it to a folder or use an installer for the application.

### Hardware requirements
* **Memory (RAM):** A minimum of 4 GB is required, but we recommend 8GB or more.
* **Processors:** We recommend a multi-core processor due to the fact that the software has been implemented to enable parallel processing of computationally intensive tasks.
* **Hard Disk:** a minimum of 500 MB of free space is required.

### Software requirements 
* Java SE Runtime Environment 8.
**Note:** It does not work (yet) with versions of Java greater than 8.

### Issues with java versions
StarPep toolbox does not yet support any version of Java > 8. The requirement is java 8. If you have multiple Java versions installed on your system, please configure starPep toolbox to run on the supported one (Java 8). Find the `etc/starPep.conf` file in the installed directory and configure
the `jdkhome=“/path/to/jdk”` accordingly. The symbol “#” at the beginint of the line means that it is commented out, please remove it.

### Increasing the memory heap size
You may increase the memory heap further if there is enough RAM available in your system (recommended). First, you have to switch to the directory where the application has been installed or extracted. Open the text file “starpep.conf” located under the etc folder. Once the file has been open, locate the default options setting and change the min/max heapsize values (-J-Xms or -J-Xmx). For instance, to increase the memory heap size from 4G to 8G, enter the value:

```bash
default_options="--branding starpep -J-Xms24m -J-Xmx8G"
```

Then save the text file `etc/starpep.conf` and run the application.

## The Team
This project was developed by members and collaborators of the *Grupo de Medicina Molecular y Traslacional (MeM&T)* at Universidad San Francisco de Quito, which is lead by [Yovani Marrero-Ponce](https://orcid.org/0000-0003-2721-1142).

## Contributing
We encourage your participation as a contributor in this project considering your interest, availability, or skill requirements. Detailed information about ways of collaborating on this project can be found in our [contributing guidelines](CONTRIBUTING.md).

## License
...

## Get in touch
If you want to report a problem or suggest an improvement, you should [open an issue](https://github.com/Grupo-Medicina-Molecular-y-Traslacional/StarPep/issues/new) at this Github repository, and we can follow your questions or suggestions. But, you can also contact Yovani by emailing ymarrero77@yahoo.es.

