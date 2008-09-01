package com.kalugin.plugins.sync.api;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.RegistryFactory;

public class SyncPluginsRegistry {
    
    private static final Pattern PREFIX = Pattern.compile("^\\s*Sync(?:hronize)? with ");
    
    private static Collection<SourceFactory> factories;
    
    public static Source forPhrase(String phrase) {
        Matcher matcher = PREFIX.matcher(phrase);
        if (!matcher.find())
            return null;
        phrase = phrase.substring(matcher.end());
        if (phrase.endsWith("."))
            phrase = phrase.substring(0, phrase.length() - 1);
        return forSubphrase(phrase);
    }

    private static Source forSubphrase(String phrase) {
        if (factories == null)
            factories = readFactories();
        for (SourceFactory factory : factories) {
            Source result = factory.forPhrase(phrase);
            if (result != null)
                return result;
        }
        return null;
    }

    private static Collection<SourceFactory> readFactories() {
        Collection<SourceFactory> result = newArrayList();
        IExtensionPoint ep = RegistryFactory.getRegistry().getExtensionPoint("com.kalugin.plugins.sync.api.sourceFactories");
        for(IConfigurationElement element : ep.getConfigurationElements())
            if ("sourceFactory".equals(element.getName()))
                readFactory(element, result);
        return result;
    }

    private static void readFactory(IConfigurationElement element, Collection<SourceFactory> result) {
        try {
            result.add((SourceFactory) element.createExecutableExtension("class"));
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
}
