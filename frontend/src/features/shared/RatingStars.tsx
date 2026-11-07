import {HStack, IconButton, RatingGroup} from "@chakra-ui/react";
import {FiX} from "react-icons/fi";

interface RatingStarsProps {
    /** Score on a 1..10 scale (shown as 5 stars with halves). */
    value: number | null | undefined;
    size?: number;
    onChange?: (score: number | null) => void;
}

export const RatingStars = ({value, size = 16, onChange}: RatingStarsProps) => {
    const stars = (value ?? 0) / 2;
    return (
        <HStack gap="1">
            <RatingGroup.Root count={5} allowHalf value={stars} disabled={!onChange} colorPalette="yellow"
                              pointerEvents={onChange ? undefined : "none"}
                              aria-label={onChange ? "Your rating" : `Rating: ${stars} of 5`}
                              onValueChange={details => onChange?.(details.value > 0 ? Math.round(details.value * 2) : null)}>
                <RatingGroup.HiddenInput/>
                <RatingGroup.Control>
                    <RatingGroup.Items fontSize={`${size}px`}/>
                </RatingGroup.Control>
            </RatingGroup.Root>
            {onChange && value != null && (
                <IconButton aria-label="Remove rating" size="2xs" variant="ghost" onClick={() => onChange(null)}>
                    <FiX/>
                </IconButton>
            )}
        </HStack>
    );
};

export default RatingStars;
