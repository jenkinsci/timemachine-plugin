package fr.loof.jenkins.timemachine;

import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Extension
public class TimeMachineItemListener extends ItemListener {

    @Inject TimeMachine timeMachine;

    @Override
    public void onCreated(Item item) {
        if (item instanceof AbstractItem) {
            final AbstractItem i = (AbstractItem) item;
            try {
                timeMachine.add(i.getDisplayName(), i.getConfigFile(), Action.CREATE);
            } catch (IOException | GitAPIException e) {
                // oups
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCopied(Item src, Item item) {
        if (item instanceof AbstractItem) {
            final AbstractItem i = (AbstractItem) item;
            try {
                timeMachine.add(i.getDisplayName() + "as a copy of " + src.getDisplayName(), i.getConfigFile(), Action.CREATE);
            } catch (IOException | GitAPIException e) {
                // oups
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeleted(Item item) {
        if (item instanceof AbstractItem) {
            final AbstractItem i = (AbstractItem) item;
            try {
                timeMachine.add(i.getDisplayName(), i.getConfigFile(), Action.DELETE);
            } catch (IOException | GitAPIException e) {
                // oups
                e.printStackTrace();
            }
        }
    }
}
