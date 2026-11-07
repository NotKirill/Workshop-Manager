import {Checkbox} from "@chakra-ui/react";
import {usePermissions} from "@/features/auth/queries.ts";
import {Field} from "@/features/shared/Field.tsx";

interface AutoBackupCheckboxProps {
    checked: boolean;
    onChange: (checked: boolean) => void;
}

export const AutoBackupCheckbox = ({checked, onChange}: AutoBackupCheckboxProps) => {
    const can = usePermissions();
    // Without the permission it can only be switched off (an item may already be backed up automatically)
    const disabled = !checked && !can("CONTENT_IMPORT");
    return (
        <Field helperText="Files are downloaded now and again after every update of the source. You can always back up or upload files by hand.">
            <Checkbox.Root checked={checked} disabled={disabled}
                           onCheckedChange={event => onChange(event.checked === true)}>
                <Checkbox.HiddenInput/>
                <Checkbox.Control/>
                <Checkbox.Label>Back up automatically</Checkbox.Label>
            </Checkbox.Root>
        </Field>
    );
};

export default AutoBackupCheckbox;
