# android-speech-diarization
Automatically exported from code.google.com/p/android-speech-diarization

This project is now longer under development, but it's here for anyone interested. If you try to run the project on 
a device, you will eventually hit an error related to a missing 'config.xml' file. I'm copying the contents of that file below.
Copy it into your own xml file, name it 'config.xml', and place it into the root directory of your device to actually run this
project (I think it's the root directory, you can look through the code to check where it expects to read this file from ...)

--------------------------------------------------- config.xml -----------------------------------------------------------------

<?xml version="1.0" encoding="UTF-8"?>
<config>
<property name="logLevel" value="WARNING"/>
<component name="mfcFrontEnd" type="edu.cmu.sphinx.frontend.FrontEnd">
<propertylist name="pipeline">
<item>streamDataSource</item>
<item>preemphasizer</item>
<item>windower</item>
<item>fft</item>
<item>melFilterBank</item>
<item>dct</item>

</propertylist>
</component>
<component name="streamDataSource"
type="edu.cmu.sphinx.frontend.util.StreamDataSource">
<property name="sampleRate" value="16000"/>
</component>
<component name="preemphasizer"
type="edu.cmu.sphinx.frontend.filter.Preemphasizer"/>
<component name="windower"
type="edu.cmu.sphinx.frontend.window.RaisedCosineWindower"/>
<component name="fft"
type="edu.cmu.sphinx.frontend.transform.DiscreteFourierTransform">
<property name="numberFftPoints" value="512"/>
</component>
<component name="melFilterBank"
type="edu.cmu.sphinx.frontend.frequencywarp.MelFrequencyFilterBank">
<property name="numberFilters" value="40"/>
<property name="minimumFrequency" value="130"/>
<property name="maximumFrequency" value="6800"/>
</component>
<component name="dct"
type="edu.cmu.sphinx.frontend.transform.DiscreteCosineTransform"/>
</config>
