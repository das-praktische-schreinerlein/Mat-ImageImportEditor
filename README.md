mat-imageimporteditor
=====================

# Desc
This is a subset of managment-tools created for the imageimport on backend of www.michas-ausflugstipps.de.

1. allinone html-editor to prepare import of image-dirs to mediadb (more infos on www.michas-ausflugstipps.de/portal-datenpflege.html)
2. imagedir-import-generator to prepare json-files for the imageimport-editor

# TODO for me
- [ ] documentation
- [ ] externalize config
- [ ] use and optimize it :-)

# History and milestones
- 2014 
   - prepared the tools for going public (documentation...) 
   - separated the public-tools
- 2014
   - initial version based on the original java+excel-tools

# Requires
- for building
   - maven
   - IDE (I built it with eclipse)
- for run
   - java
   - browser with html5-support

# Install
- save the project to 
```bat
d:\public_projects\MatImageImportEditor
```

- import project to Eclipse

- run maven 
```bat
cd d:\public_projects\MatImageImportEditor
mvn compile
mvn org.apache.maven.plugins:maven-assembly-plugin:assembly
```

# Example
- save your jpg-images at 
```bat
D:\Bilder\digifotos\test\20140304-berlin\
D:\Bilder\digifotos\test\20140305-muenchen\
D:\Bilder\digifotos\test\20140307-hamburg\
```

- run generator
```bat
echo off
cd d:\public_projects\MatImageImportEditor
sbin\genImageDirImportEntries.bat D:\Bilder\digifotos\test > d:\tmp\importDigiFotos-test.json
```

- open editor
```bat
cd d:\public_projects\MatImageImportEditor
firefox web\ImageImportEditor.html
```

- open jsonfile in firefox:ImageImportEditor.html
```bat
d:\tmp\importDigiFotos-test.json
```

# License
```
/**
 * @author Michael Schreiner <ich@michas-ausflugstipps.de>
 * @category publishing
 * @copyright Copyright (c) 2005-2014, Michael Schreiner
 * @license http://mozilla.org/MPL/2.0/ Mozilla Public License 2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
```
