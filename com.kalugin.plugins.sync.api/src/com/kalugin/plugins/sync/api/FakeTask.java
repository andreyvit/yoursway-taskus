package com.kalugin.plugins.sync.api;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;

public class FakeTask implements SynchronizableTask {
    
    private static final class FakeSynchronizableTag implements SynchronizableTag {
        
        private final String name;
        private final String value;

        public FakeSynchronizableTag(String name, String value) {
            if (name == null)
                throw new NullPointerException("name is null");
            if (value == null)
                throw new NullPointerException("value is null");
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean nameEquals(String tagName) {
            return name.equals(tagName);
        }
        
        public boolean valueEquals(SynchronizableTag another) {
            if (!another.getName().equals(name))
                throw new IllegalArgumentException("Must be comparing with the same kind of tag");
            String peerValue = another.getValue();
            return value == null && peerValue == null || value != null && value.equals(peerValue);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FakeSynchronizableTag other = (FakeSynchronizableTag) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        public String getValue() {
            return value;
        }
    }

    private TaskId id;
    private String name;
    private SynchronizableTag fakeTag;
    private FakeSynchronizableTag idTag;

    public FakeTask(int index, String idTagName) {
        this.id = new TaskId("F" + index);
        this.name = "Fake task " + index;
        fakeTag = new FakeSynchronizableTag("faketag", "V" + (int) (Math.random() * 100));
        idTag = new FakeSynchronizableTag(idTagName, id.stringValue());
    }

    public TaskId getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Collection<SynchronizableTag> tags() {
        return newArrayList(fakeTag, idTag);
    }
    
    public String toStringWithoutTags() {
        return name + " #" + id;
    }
    
}
